import { AfterViewInit, Component, ElementRef, Inject, OnDestroy, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import Hls from 'hls.js';
import { CameraCadastro } from '../../../../logic/services/cadastro.service';

@Component({
  selector: 'app-camera-view-modal',
  templateUrl: './camera-view-modal.component.html',
  styleUrls: ['./camera-view-modal.component.scss'],
  standalone: true,
})
export class CameraViewModalComponent implements AfterViewInit, OnDestroy {
  @ViewChild('videoPlayer', { static: true }) videoRef!: ElementRef<HTMLVideoElement>;
  erro = '';
  private hls?: Hls;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: CameraCadastro,
    public dialog: MatDialogRef<CameraViewModalComponent>
  ) {}

  ngAfterViewInit(): void {
    const video = this.videoRef.nativeElement;
    const streamUrl = 'http://localhost:3000/video';

    video.addEventListener('error', () => this.erro = 'Transmissão indisponível no momento.');
    if (video.canPlayType('application/vnd.apple.mpegurl')) {
      video.src = streamUrl;
    } else if (Hls.isSupported()) {
      this.hls = new Hls();
      this.hls.loadSource(streamUrl);
      this.hls.attachMedia(video);

      this.hls.on(Hls.Events.ERROR, (_event, data) => {
        if (data.fatal) this.erro = 'Transmissão indisponível no momento.';
      });
    } else {
      this.erro = 'Este navegador não suporta a transmissão.';
    }
  }

  ngOnDestroy(): void {
    this.hls?.destroy();
    this.videoRef?.nativeElement.pause();
  }
}
