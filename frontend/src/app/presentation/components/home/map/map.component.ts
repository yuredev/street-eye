import { Component, AfterViewInit, inject } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import * as L from 'leaflet';
import { CameraViewModalComponent } from '../camera-view-modal/camera-view-modal.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { CameraService } from '../../../../logic/services/camera/camera.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
  standalone: true,
  imports: [MatToolbarModule, MatIconModule, MatDialogModule],
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;

  constructor(
    private cameraService: CameraService,
    private dialog: MatDialog
  ) {}

  private initMap(): void {
    this.map = L.map('map', {
      center: [-5.8238832, -35.2045377],
      zoom: 90,
    });

    const tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 18,
        minZoom: 3,
        attribution:
          '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }
    );

    tiles.addTo(this.map);
  }

  private loadMarkers(): void {
    this.cameraService.getCameras().subscribe({
      next: (cameras) => {
        cameras.forEach((cam) => {
          const { latitude, longitude } = cam.localizacao;

          const icone = L.divIcon({
            html: `<i class="material-icons" style="color: #7700FF; font-size: 32px;">videocam</i>`,
            iconSize: [30, 30],
          });
          const marker = L.marker([latitude, longitude], {
            icon: icone,
          }).addTo(this.map);
          marker.on('click', () => this.openModal());
        });
      },
      error: (error) => {
        alert('Erro ao buscar cameras');
      },
    });
  }

  private openModal(): void {
    this.dialog.open(CameraViewModalComponent, {
      data: { message: 'osjgoisjdg' },
      width: '1800px',
      minWidth: '780px',
    });
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.loadMarkers();
    this.map.invalidateSize();
  }
}
