import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { ApiService } from '../../services/api.service';
import { FilterService } from '../../services/filter.service';
import type { PetRequestBody, ProposalItem, ProposalResponse } from '../../app.model';

@Injectable({
  providedIn: 'root',
})
export class PetApiService {
  private apiService = inject(ApiService);
  private filterService = inject(FilterService);
  private readonly petList$ = signal<ProposalItem[]>([]);

  readonly petList = this.petList$.asReadonly();


  constructor() {
    this.getProposalList().subscribe();
  }

  addProposal(proposalData: PetRequestBody): Observable<any> {
    return this.apiService.addProposal(proposalData);
  }

  getProposalList(): Observable<ProposalResponse> {
    const query = this.filterService.getFilterQuery();

    return this.apiService.getProposalList(query).pipe(tap(res => this.petList$.set(res.items)));
  }

  getProposal(id: number): Observable<ProposalItem> {
    return this.apiService.getProposal(id);
  }

  updateProposal(proposalId: number, proposalData: Partial<any>): Observable<any> {
    return this.apiService.updateProposal(proposalId, proposalData);
  }
}
