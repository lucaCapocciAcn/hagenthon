import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_BASE = 'http://localhost:8080';

export interface Question {
  fieldName: string;
  originalLabel: string;
  simpleQuestion: string;
}

export interface UploadResponse {
  sessionId: string;
  questions: Question[];
}

@Injectable({ providedIn: 'root' })
export class FormService {
  private http = inject(HttpClient);

  upload(file: File): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<UploadResponse>(`${API_BASE}/api/forms/upload`, formData);
  }

  submitAnswers(sessionId: string, answers: Record<string, string>): Observable<Blob> {
    return this.http.post(
      `${API_BASE}/api/forms/${sessionId}/answers`,
      { answers },
      { responseType: 'blob' }
    );
  }
}
