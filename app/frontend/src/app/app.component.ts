import {
  Component,
  signal,
  computed,
  inject,
  ViewChild,
  ElementRef,
  AfterViewChecked
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormService, Question } from './form.service';

type Screen = 'upload' | 'chat';

@Component({
    selector: 'app-root',
    imports: [CommonModule],
    templateUrl: './app.component.html',
    styleUrl: './app.component.css'
})
export class AppComponent implements AfterViewChecked {
  private formService = inject(FormService);

  @ViewChild('answerInput') answerInputRef?: ElementRef<HTMLInputElement>;

  // ── State ──────────────────────────────────────────────────────────────────
  screen = signal<Screen>('upload');
  sessionId = signal<string>('');
  questions = signal<Question[]>([]);
  currentIndex = signal<number>(0);
  answers: Record<string, string> = {};
  currentAnswer = signal<string>('');

  // Upload state
  uploading = signal<boolean>(false);
  uploadError = signal<string>('');

  // Download state
  downloading = signal<boolean>(false);

  // Focus management
  private shouldFocus = false;

  // ── Computed ───────────────────────────────────────────────────────────────
  total = computed(() => this.questions().length);
  currentQuestion = computed(() => this.questions()[this.currentIndex()]);
  isLast = computed(() => this.currentIndex() === this.total() - 1);
  progressLabel = computed(
    () => `Campo ${this.currentIndex() + 1} di ${this.total()}`
  );
  progressPercent = computed(() =>
    this.total() > 0 ? ((this.currentIndex() + 1) / this.total()) * 100 : 0
  );

  // ── Upload ─────────────────────────────────────────────────────────────────
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.uploadFile(file);
  }

  private uploadFile(file: File): void {
    this.uploading.set(true);
    this.uploadError.set('');

    this.formService.upload(file).subscribe({
      next: (response) => {
        this.sessionId.set(response.sessionId);
        this.questions.set(response.questions);
        this.currentIndex.set(0);
        this.answers = {};
        this.currentAnswer.set('');
        this.uploading.set(false);
        this.screen.set('chat');
        this.shouldFocus = true;
      },
      error: (err) => {
        console.error('Errore caricamento:', err);
        this.uploadError.set(
          'Non è stato possibile caricare il modulo. Riprova.'
        );
        this.uploading.set(false);
      }
    });
  }

  // ── Chat navigation ────────────────────────────────────────────────────────
  onNext(): void {
    const q = this.currentQuestion();
    if (!q) return;

    const answer = this.currentAnswer().trim();
    this.answers[q.fieldName] = answer;

    if (this.isLast()) {
      this.downloadPdf();
    } else {
      this.currentIndex.update((i) => i + 1);
      this.currentAnswer.set('');
      this.shouldFocus = true;
    }
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.onNext();
    }
  }

  // ── PDF download ───────────────────────────────────────────────────────────
  private downloadPdf(): void {
    this.downloading.set(true);
    this.formService.submitAnswers(this.sessionId(), this.answers).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'modulo-compilato.pdf';
        a.click();
        URL.revokeObjectURL(url);
        this.downloading.set(false);
        // Return to upload screen after download
        this.screen.set('upload');
      },
      error: (err) => {
        console.error('Errore download:', err);
        this.downloading.set(false);
      }
    });
  }

  // ── Lifecycle ──────────────────────────────────────────────────────────────
  ngAfterViewChecked(): void {
    if (this.shouldFocus && this.answerInputRef) {
      // Imperatively clear the DOM value: Angular's [value] binding skips the
      // update when the element was user-modified and the new signal value is
      // the same as what Angular last SET (not what the user typed).
      this.answerInputRef.nativeElement.value = '';
      this.answerInputRef.nativeElement.focus();
      this.shouldFocus = false;
    }
  }
}
