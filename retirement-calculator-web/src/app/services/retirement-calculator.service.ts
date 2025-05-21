import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { RetirementCalculationRequest } from '../models/retirement-request.model';
import { RetirementCalculationResponse } from '../models/retirement-response.model';

@Injectable({
  providedIn: 'root'
})
export class RetirementCalculatorService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/calculator';

  constructor(private http: HttpClient) { }

  calculateRetirement(request: RetirementCalculationRequest): Observable<RetirementCalculationResponse> {
    return this.http.post<RetirementCalculationResponse>(`${this.apiUrl}/retirement`, request)
      .pipe(
        map(response => this.validateResponse(response)),
        catchError(this.handleError)
      );
  }

  private validateResponse(response: RetirementCalculationResponse): RetirementCalculationResponse {
    if (!response || typeof response.totalRetirementSavings !== 'number' || 
        typeof response.monthlyDeposit !== 'number' || 
        typeof response.yearsToRetirement !== 'number') {
      throw new Error('Invalid response format from server');
    }
    return response;
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred while calculating retirement savings.';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.error?.error) {
        errorMessage = error.error.error;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }
} 