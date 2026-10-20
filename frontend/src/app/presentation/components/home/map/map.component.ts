import { Component, AfterViewInit, EventEmitter, Input, OnChanges, OnDestroy, Output } from '@angular/core';
import * as L from 'leaflet';
import { CameraCadastro } from '../../../../logic/services/cadastro.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
  standalone: true,
  imports: [],
})
export class MapComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() cameras: CameraCadastro[] = [];
  @Input() modoAdicionar = false;
  @Output() localSelecionado = new EventEmitter<{ latitude: number; longitude: number }>();
  @Output() cameraSelecionada = new EventEmitter<CameraCadastro>();
  private map!: L.Map;
  private markers = L.layerGroup();
  private resizeObserver?: ResizeObserver;

  private initMap(): void {
    this.map = L.map('map', {
      center: [-5.8238832, -35.2045377],
      zoom: 13,
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
    this.markers.addTo(this.map);
    this.map.on('click', event => {
      if (this.modoAdicionar) this.localSelecionado.emit({
        latitude: event.latlng.lat, longitude: event.latlng.lng
      });
    });
  }

  private drawMarkers(): void {
    if (!this.map) return;
    this.markers.clearLayers();
    for (const camera of this.cameras) {
      const icon = L.divIcon({
        html: '<i class="material-icons" style="color: #7700FF; font-size: 32px;">videocam</i>',
        iconSize: [30, 30],
      });
      L.marker([camera.latitude, camera.longitude], { icon, bubblingMouseEvents: false })
        .bindTooltip(camera.nome)
        .on('click', () => {
          if (!this.modoAdicionar) this.cameraSelecionada.emit(camera);
        })
        .addTo(this.markers);
    }
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.drawMarkers();
    this.resizeObserver = new ResizeObserver(() => this.map.invalidateSize());
    this.resizeObserver.observe(this.map.getContainer());
    this.map.invalidateSize();
  }

  ngOnChanges(): void { this.drawMarkers(); }
  ngOnDestroy(): void { this.resizeObserver?.disconnect(); this.map?.remove(); }
}
