import { Location } from "../../data/models/localizacao";

export abstract class LocationUtils {
  static locationToArray({ latitude, longitude }: Location): [number, number] {
    return [latitude, longitude];
  }

  static arrayToLocation([latitude, longitude]: [number, number]): Location {
    return {
      latitude,
      longitude,
    };
  }
}
