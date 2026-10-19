import {
  AfterViewInit,
  Component,
  ElementRef,
  Inject,
  ViewChild,
} from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import Hls from 'hls.js';

@Component({
  selector: 'app-camera-view-modal',
  templateUrl: './camera-view-modal.component.html',
  styleUrls: ['./camera-view-modal.component.scss'],
  standalone: true,
})
export class CameraViewModalComponent implements AfterViewInit {
  @ViewChild('videoPlayer', { static: true })
  videoRef!: ElementRef<HTMLVideoElement>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}

  ngAfterViewInit(): void {
    const video = this.videoRef.nativeElement;
    const streamUrl = 'http://localhost:3000/video';

    // se estivermos em um browser que suporta é só jogar a URL na tag vídeo
    if (video.canPlayType('application/vnd.apple.mpegurl')) {
      video.src = streamUrl;
    } else if (Hls.isSupported()) {
      const hls = new Hls();
      hls.loadSource(streamUrl);
      hls.attachMedia(video);

      hls.on(Hls.Events.ERROR, (event, data) => {
        console.error('HLS.js error', data);
      });
    } else {
      console.error('HLS não suportado nesse navegador');
    }
  }
}
