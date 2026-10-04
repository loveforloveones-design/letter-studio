import React from 'react';
import { LetterData } from '../types';
import { WaxSealBadge } from './WaxSeals';
import { PaperTextureOverlay } from './PaperTextureOverlay';

interface LetterCanvasProps {
  letter: LetterData;
  interactive?: boolean;
  canvasRef?: React.RefObject<HTMLDivElement | null>;
  className?: string;
}

const PAPER_BG_CLASSES: Record<string, string> = {
  cream_laid: 'bg-[#fffdf8]',
  vintage_parchment: 'bg-[#fdf6df]',
  blush_rose: 'bg-[#fceef3]',
  sage_mint: 'bg-[#eef5ee]',
  sky_pastel: 'bg-[#edf4fa]',
  crisp_white: 'bg-[#ffffff]',
};

export const LetterCanvas: React.FC<LetterCanvasProps> = ({
  letter,
  interactive = true,
  canvasRef,
  className = '',
}) => {
  const fontClass =
    letter.fontChoice === 'handwritten'
      ? 'font-handwriting'
      : letter.fontChoice === 'serif_classic'
      ? 'font-serif-classic'
      : letter.fontChoice === 'mono_typewriter'
      ? 'font-typewriter'
      : 'font-urbanist';

  return (
    <div
      ref={canvasRef}
      className={`relative w-full aspect-[1/1.414] max-w-[620px] mx-auto rounded-2xl border-2 border-[#33314e] shadow-[4px_4px_0px_#33314e] p-6 sm:p-10 flex flex-col justify-between overflow-hidden select-text transition-colors duration-200 ${
        PAPER_BG_CLASSES[letter.paperTheme] || 'bg-[#fffdf8]'
      } ${letter.showRuledLines ? 'ruled-paper' : ''} ${className}`}
      id="printable-letterhead"
    >
      {/* Paper Background Texture */}
      <PaperTextureOverlay
        texture={letter.paperTexture}
        intensity={letter.textureIntensity}
      />

      {/* Letter Content Container */}
      <div className="relative z-10 flex flex-col h-full justify-between">
        {/* TOP SECTION: Letterhead Header */}
        <div>
          {letter.headerStyle === 'corporate_official' && (
            <div className="border-b-2 border-[#33314e] pb-3 mb-4">
              <div className="flex items-start justify-between">
                <div>
                  <h2 className="text-base sm:text-lg font-extrabold tracking-wider text-[#33314e] uppercase font-urbanist">
                    {letter.senderOrg || 'VANGUARD INITIATIVE'}
                  </h2>
                  {letter.senderTitle && (
                    <p className="text-xs text-[#514e6e] font-medium">{letter.senderTitle}</p>
                  )}
                </div>
                <div className="text-right text-[11px] text-[#514e6e] space-y-0.5 font-urbanist">
                  <p className="font-semibold text-[#33314e]">{letter.date}</p>
                  {letter.referenceNumber && (
                    <p className="text-[#7e7c97] tracking-wider">{letter.referenceNumber}</p>
                  )}
                </div>
              </div>
              <div className="mt-2 pt-2 border-t border-[#33314e]/20 flex items-center justify-between text-[10px] text-[#514e6e] font-urbanist">
                <span>{letter.senderAddress || '104 Studio Lane, Berlin'}</span>
                <span>
                  {letter.senderEmail} · {letter.senderPhone}
                </span>
              </div>
            </div>
          )}

          {letter.headerStyle === 'minimal_modern' && (
            <div className="border-b border-[#33314e] pb-3 mb-4 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-[#33314e] text-[#fffdf8] flex items-center justify-center font-bold text-sm">
                  {letter.initialMonogram || 'S'}
                </div>
                <div>
                  <span className="font-bold text-xs uppercase tracking-wider text-[#33314e] block">
                    {letter.sender || 'Sabi'}
                  </span>
                  <span className="text-[11px] text-[#7e7c97]">
                    {letter.senderOrg || 'Independent Studio'}
                  </span>
                </div>
              </div>
              <span className="text-xs text-[#7e7c97]">{letter.date}</span>
            </div>
          )}

          {letter.headerStyle === 'vintage_crest' && (
            <div className="border-b border-[#33314e]/40 pb-3 mb-4 text-center">
              <p className="text-[11px] font-serif-classic uppercase tracking-[0.2em] text-[#33314e] font-bold">
                ✦ {letter.senderOrg || 'EX LIBRIS ET EPISTOLA'} ✦
              </p>
              <p className="text-[10px] italic font-serif-classic text-[#514e6e] mt-0.5">
                — From the Desk of {letter.sender || 'Sabi'} —
              </p>
              <div className="flex justify-between items-center text-[10px] text-[#7e7c97] mt-2 font-serif-classic">
                <span>Addressed to: {letter.recipient}</span>
                <span>{letter.date}</span>
              </div>
            </div>
          )}

          {letter.headerStyle === 'artisan_botanical' && (
            <div className="border-b border-[#33314e]/25 pb-3 mb-4 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span className="text-xl">🌿</span>
                <div>
                  <h3 className="font-serif-classic font-bold text-sm text-[#33314e]">
                    {letter.senderOrg || 'The Artisan Press'}
                  </h3>
                  <p className="text-[10px] italic text-[#7e7c97]">
                    Handcrafted notes · {letter.sender || 'Sabi'}
                  </p>
                </div>
              </div>
              <span className="text-xs text-[#33314e] font-serif-classic">{letter.date}</span>
            </div>
          )}

          {/* Subject Line (optional) */}
          {letter.subject && (
            <div className="mb-3">
              <span className="text-xs sm:text-sm font-bold text-[#33314e] underline decoration-[#f06e9a] underline-offset-4">
                {letter.subject}
              </span>
            </div>
          )}

          {/* Salutation */}
          <div className="mb-3">
            <h3 className={`text-base sm:text-lg font-semibold text-[#33314e] ${fontClass}`}>
              {letter.salutation || `Dear ${letter.recipient},`}
            </h3>
          </div>

          {/* Body Text */}
          <div
            className={`text-[#33314e] leading-relaxed whitespace-pre-line ${fontClass}`}
            style={{ fontSize: `${letter.fontSize}px` }}
          >
            {letter.bodyText}
          </div>

          {/* Optional Polaroid Photo Card */}
          {letter.hasPolaroid && (
            <div
              className="mt-4 p-2 bg-[#fffdf8] border-2 border-[#33314e] rounded shadow-md w-36 sm:w-44 select-none relative"
              style={{ transform: `rotate(${letter.polaroidRotation}deg)` }}
            >
              {/* Washi tape topper */}
              <div className="absolute -top-2 left-1/2 -translate-x-1/2 w-12 h-3.5 bg-[#ffd75e]/90 border border-[#33314e]/40 -rotate-2" />
              <div className="aspect-square bg-[#efe8da] border border-[#33314e]/20 rounded-sm overflow-hidden flex items-center justify-center">
                {letter.polaroidImage ? (
                  <img
                    src={letter.polaroidImage}
                    alt="Memory"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <span className="text-3xl">📷</span>
                )}
              </div>
              <p className="font-handwriting text-xs text-[#33314e] text-center mt-1.5 truncate">
                {letter.polaroidCaption || 'our afternoon in Paris'}
              </p>
            </div>
          )}
        </div>

        {/* BOTTOM SECTION: Sign-off, Signature & Wax Seal */}
        <div className="mt-6 pt-4 flex items-end justify-between border-t border-transparent">
          <div className="text-[10px] text-[#7e7c97]">
            {letter.referenceNumber && (
              <span className="tracking-widest uppercase">{letter.referenceNumber}</span>
            )}
          </div>

          <div className="flex flex-col items-end">
            <p className={`text-sm italic text-[#514e6e] ${fontClass}`}>
              {letter.signOff || 'With all my love,'}
            </p>

            <div className="flex items-center gap-3 mt-1">
              <span className="font-handwriting text-2xl sm:text-3xl text-[#33314e] select-none">
                {letter.signatureText || letter.sender || 'Sabi'}
              </span>

              {/* Stamped Wax Seal */}
              <WaxSealBadge
                style={letter.sealStyle}
                color={letter.sealColor}
                size={50}
                monogramChar={letter.initialMonogram}
              />
            </div>

            {(letter.senderTitle || letter.senderOrg) && (
              <p className="text-[10px] text-[#7e7c97] mt-0.5">
                {[letter.senderTitle, letter.senderOrg].filter(Boolean).join(' · ')}
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
