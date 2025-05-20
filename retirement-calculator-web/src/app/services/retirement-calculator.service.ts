import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RetirementCalculationRequest {
  currentAge: number;
  retirementAge: number;
  interestRate: number;
  lifestyleType: 'simple' | 'fancy';
}

export interface RetirementCalculationResponse {
  futureValue: number;
  totalDeposits: number;
  interestEarned: number;
}

@Injectable({
  providedIn: 'root'
})
export class RetirementCalculatorService {
  private apiUrl = 'http://localhost:8080/api/v1/calculator'; // Update this with your actual API endpoint

  constructor(private http: HttpClient) { }

  calculateRetirement(request: RetirementCalculationRequest): Observable<RetirementCalculationResponse> {
    return this.http.post<RetirementCalculationResponse>(`${this.apiUrl}/calculate`, request);
  }
} 