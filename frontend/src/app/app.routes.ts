import { Routes } from '@angular/router';
import { WorkflowStateService } from './core/services/workflow-state.service';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'admin',
    loadComponent: () => import('./pages/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
  },
  {
    path: 'mbl-processing',
    providers: [WorkflowStateService],
    loadComponent: () => import('./pages/mbl-flow/mbl-flow.component').then(m => m.MblFlowComponent)
  },
  {
    path: 'invoice-processing',
    loadComponent: () => import('./pages/invoice-flow/invoice-flow.component').then(m => m.InvoiceFlowComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];