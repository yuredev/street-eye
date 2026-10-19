import { Location } from "./localizacao";
import { User } from "./usuario";

export interface Camera {
  localizacao: Location;
  user: User;
  id: number;
}
