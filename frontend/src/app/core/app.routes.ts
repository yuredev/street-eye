import { Routes } from '@angular/router';
import { HomeComponent } from '../presentation/pages/home/home.component';
import { LoginComponent } from '../presentation/pages/login/login.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'login', component: LoginComponent,
  },
];
