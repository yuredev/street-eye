import { Routes } from '@angular/router';
import { HomeComponent } from '../presentation/pages/home/home.component';
import { LoginComponent } from '../presentation/pages/login/login.component';
import { ComunidadesComponent } from '../presentation/pages/comunidades/comunidades.component';
import { PessoasComponent } from '../presentation/pages/pessoas/pessoas.component';
import { CamerasComponent } from '../presentation/pages/cameras/cameras.component';
import { PerfilComponent } from '../presentation/pages/perfil/perfil.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'comunidades', component: ComunidadesComponent },
  { path: 'pessoas', component: PessoasComponent },
  { path: 'cameras', component: CamerasComponent },
  { path: 'perfil', component: PerfilComponent },
  { path: 'gerenciar', redirectTo: 'comunidades', pathMatch: 'full' },
  {
    path: 'login', component: LoginComponent,
  },
];
