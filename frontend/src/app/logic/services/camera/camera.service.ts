import { Injectable } from '@angular/core';
import { Observable, Observer } from 'rxjs';
import { LocationUtils } from '../../utils/location-utils';
import { Camera } from '../../../data/models/camera';

@Injectable({
  providedIn: 'root',
})
export class CameraService {
  constructor() {}

  getCameras(): Observable<Array<Camera>> {
    return new Observable((subscriber) => {
      setTimeout(() => {
        subscriber.next([
          {
            localizacao: LocationUtils.arrayToLocation([
              -5.8238832, -35.2045377,
            ]),
            user: {
              id: 5325,
              name: 'João Silva',
              login: 'joao123',
            },
            id: 5959595,
          },
          {
            localizacao: LocationUtils.arrayToLocation([
              -5.8230201, -35.2047877,
            ]),
            user: {
              id: 5325,
              name: 'João Silva',
              login: 'joao123',
            },
            id: 5959595,
          },
        ]);
      }, 500);
    });
  }
}
