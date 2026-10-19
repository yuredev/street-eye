import { Component } from '@angular/core';
import { MapComponent } from '../../components/home/map/map.component';
import { HomePageToolbarComponent } from '../../components/home/home-page-toolbar/home-page-toolbar.component';

@Component({
  selector: 'app-home',
  imports: [MapComponent, HomePageToolbarComponent],
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {}
