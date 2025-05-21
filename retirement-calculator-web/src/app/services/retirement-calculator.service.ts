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
  currentAge: number;
  retirementAge: number;
  interestRate: number;
  lifestyleType: string;
  totalRetirementSavings: number;
  monthlyDeposit: number;
  yearsToRetirement: number;
}

@Injectable({
  providedIn: 'root'
})
export class RetirementCalculatorService {
  private apiUrl = 'http://localhost:8080/api/v1/calculator';

  constructor(private http: HttpClient) { }

  calculateRetirement(request: RetirementCalculationRequest): Observable<RetirementCalculationResponse> {
    return this.http.post<RetirementCalculationResponse>(`${this.apiUrl}/retirement`, request);
  }
} 