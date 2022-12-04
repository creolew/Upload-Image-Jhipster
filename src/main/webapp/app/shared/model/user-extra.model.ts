import { IUser } from 'app/shared/model/user.model';

export interface IUserExtra {
  id?: number;
  frontImage?: string | null;
  backImage?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IUserExtra> = {};
