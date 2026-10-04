export type HeaderStyle =
  | 'none'
  | 'romantic_personal'
  | 'corporate_official'
  | 'minimal_modern'
  | 'vintage_crest'
  | 'artisan_botanical';

export type PaperTheme =
  | 'cream_laid'
  | 'vintage_parchment'
  | 'blush_rose'
  | 'sage_mint'
  | 'sky_pastel'
  | 'crisp_white';

export type PaperTexture =
  | 'grain'
  | 'fiber'
  | 'rough'
  | 'linen'
  | 'laid'
  | 'smooth';

export type FontChoice =
  | 'handwritten'
  | 'serif_classic'
  | 'mono_typewriter'
  | 'sans_clean';

export type SealStyle =
  | 'heart'
  | 'bow'
  | 'lily'
  | 'teddy'
  | 'star'
  | 'rose_crest'
  | 'monogram';

export type SealColor =
  | 'crimson'
  | 'petal_rose'
  | 'teddy_caramel'
  | 'royal_navy'
  | 'sage_olive'
  | 'antique_gold'
  | 'midnight_ink';

export type StickerType =
  | 'stamp'
  | 'bow'
  | 'lily'
  | 'heart'
  | 'coffee'
  | 'star'
  | 'teddy'
  | 'botanical'
  | 'washi';

export interface LetterSticker {
  id: string;
  type: StickerType;
  x: number; // percentage 0..100
  y: number; // percentage 0..100
  rotation: number; // -20..20 deg
  scale: number; // 0.8..1.4
}

export interface LetterData {
  recipient: string;
  recipientEmail: string;
  salutation: string;

  sender: string;
  senderTitle: string;
  senderOrg: string;
  senderAddress: string;
  senderEmail: string;
  senderPhone: string;
  senderWebsite: string;
  referenceNumber: string;
  date: string;

  subject: string;
  bodyText: string;

  signOff: string;
  signatureText: string;
  initialMonogram: string;

  headerStyle: HeaderStyle;
  paperTheme: PaperTheme;
  paperTexture: PaperTexture;
  textureIntensity: 'subtle' | 'medium' | 'deep';
  fontChoice: FontChoice;
  fontSize: number; // px 14..22
  sealStyle: SealStyle;
  sealColor: SealColor;
  showRuledLines: boolean;

  stickers: LetterSticker[];

  hasPolaroid: boolean;
  polaroidCaption: string;
  polaroidImage: string;
  polaroidRotation: number;
}
