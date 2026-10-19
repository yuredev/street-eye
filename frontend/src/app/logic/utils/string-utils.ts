export abstract class StringUtils {
  static getInitials(name: string) {
    const initials = name.trim().split(' ');
    if (initials.length === 1) {
      return initials[0][0];
    }
    return initials[0][0] + initials[1][0];
  }
}
