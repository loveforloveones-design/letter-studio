import { LetterData } from '../types';

export function encodeLetter(letter: LetterData): string {
  try {
    const jsonStr = JSON.stringify(letter);
    // URL-safe base64 encoding with Unicode support
    const base64 = btoa(
      encodeURIComponent(jsonStr).replace(/%([0-9A-F]{2})/g, (_, p1) =>
        String.fromCharCode(parseInt(p1, 16))
      )
    );
    return encodeURIComponent(base64);
  } catch (e) {
    console.error('Failed to encode letter', e);
    return '';
  }
}

export function decodeLetter(encodedStr: string): LetterData | null {
  try {
    const decodedUri = decodeURIComponent(encodedStr);
    const jsonStr = decodeURIComponent(
      Array.prototype.map
        .call(atob(decodedUri), (c: string) => {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join('')
    );
    const data = JSON.parse(jsonStr);
    if (data && typeof data === 'object' && (data.recipient || data.bodyText)) {
      return data as LetterData;
    }
    return null;
  } catch (e) {
    console.error('Failed to decode letter', e);
    return null;
  }
}

export function getLetterShareableUrl(letter: LetterData): string {
  const encoded = encodeLetter(letter);
  if (typeof window === 'undefined') return '';
  const origin = window.location.origin;
  const pathname = window.location.pathname;
  return `${origin}${pathname}?letter=${encoded}`;
}

export function getInitialLetterFromUrl(): LetterData | null {
  if (typeof window === 'undefined') return null;
  const searchParams = new URLSearchParams(window.location.search);
  const letterParam = searchParams.get('letter');
  if (letterParam) {
    return decodeLetter(letterParam);
  }

  // Also check hash fallback #letter=...
  if (window.location.hash && window.location.hash.includes('letter=')) {
    const hashParams = new URLSearchParams(window.location.hash.replace(/^#/, ''));
    const hashLetter = hashParams.get('letter');
    if (hashLetter) {
      return decodeLetter(hashLetter);
    }
  }

  return null;
}
