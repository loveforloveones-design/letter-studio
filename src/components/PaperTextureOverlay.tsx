import React from 'react';
import { PaperTexture } from '../types';

interface PaperTextureOverlayProps {
  texture: PaperTexture;
  intensity: 'subtle' | 'medium' | 'deep';
  className?: string;
}

export const PaperTextureOverlay: React.FC<PaperTextureOverlayProps> = ({
  texture,
  intensity,
  className = '',
}) => {
  if (texture === 'smooth') return null;

  const opacity =
    intensity === 'subtle' ? 0.35 : intensity === 'medium' ? 0.55 : 0.8;

  return (
    <div
      className={`absolute inset-0 pointer-events-none select-none overflow-hidden rounded-2xl z-0 ${className}`}
      style={{ opacity }}
    >
      {/* 1. FINE GRAIN / COLD PRESS NOISE */}
      {texture === 'grain' && (
        <svg className="w-full h-full object-cover" xmlns="http://www.w3.org/2000/svg">
          <filter id="grain-filter" x="0%" y="0%" width="100%" height="100%">
            <feTurbulence
              type="fractalNoise"
              baseFrequency="0.75"
              numOctaves="3"
              stitchTiles="stitch"
            />
            <feColorMatrix
              type="matrix"
              values="0 0 0 0 0.2
                      0 0 0 0 0.19
                      0 0 0 0 0.3
                      0 0 0 0.25 0"
            />
          </filter>
          <rect width="100%" height="100%" filter="url(#grain-filter)" />
        </svg>
      )}

      {/* 2. HANDMADE PAPER FIBER / SPECKLED */}
      {texture === 'fiber' && (
        <div className="w-full h-full relative">
          {/* Base micro noise */}
          <svg className="absolute inset-0 w-full h-full" xmlns="http://www.w3.org/2000/svg">
            <filter id="fiber-noise">
              <feTurbulence type="fractalNoise" baseFrequency="0.6" numOctaves="2" />
              <feColorMatrix type="matrix" values="0 0 0 0 0.3 0 0 0 0 0.25 0 0 0 0 0.2 0 0 0 0.18 0" />
            </filter>
            <rect width="100%" height="100%" filter="url(#fiber-noise)" />
          </svg>

          {/* Organic scattered fiber filaments */}
          <svg className="absolute inset-0 w-full h-full opacity-60" viewBox="0 0 400 600" preserveAspectRatio="none">
            <g stroke="#514e6e" strokeWidth="0.8" fill="none" strokeLinecap="round" opacity="0.4">
              <path d="M40 80 Q 48 95, 55 90" />
              <path d="M120 40 Q 130 45, 138 38" />
              <path d="M280 110 Q 290 125, 298 120" />
              <path d="M350 70 Q 362 65, 368 78" />
              <path d="M70 240 Q 82 250, 92 244" />
              <path d="M190 190 Q 200 185, 208 198" />
              <path d="M310 280 Q 320 295, 332 290" />
              <path d="M45 390 Q 52 405, 62 400" />
              <path d="M160 360 Q 172 355, 180 368" />
              <path d="M260 420 Q 275 430, 285 422" />
              <path d="M340 380 Q 355 390, 362 382" />
              <path d="M90 520 Q 102 535, 115 528" />
              <path d="M220 500 Q 230 495, 240 508" />
              <path d="M310 540 Q 325 550, 335 542" />
            </g>
            {/* Fine botanical specks */}
            <g fill="#7e7c97" opacity="0.35">
              <circle cx="85" cy="140" r="1.2" />
              <circle cx="210" cy="90" r="0.9" />
              <circle cx="340" cy="180" r="1.4" />
              <circle cx="60" cy="310" r="1.1" />
              <circle cx="170" cy="270" r="0.8" />
              <circle cx="290" cy="350" r="1.3" />
              <circle cx="130" cy="460" r="1.0" />
              <circle cx="250" cy="560" r="1.2" />
              <circle cx="370" cy="490" r="0.9" />
            </g>
          </svg>
        </div>
      )}

      {/* 3. HEAVY ROUGH ARTISANAL RAG */}
      {texture === 'rough' && (
        <svg className="w-full h-full object-cover" xmlns="http://www.w3.org/2000/svg">
          <filter id="rough-paper-lighting" x="0%" y="0%" width="100%" height="100%">
            <feTurbulence
              type="turbulence"
              baseFrequency="0.04"
              numOctaves="4"
              result="turbulence"
            />
            <feDiffuseLighting
              in="turbulence"
              lightingColor="#ffffff"
              surfaceScale="2"
              result="light"
            >
              <feDistantLight azimuth="45" elevation="60" />
            </feDiffuseLighting>
            <feBlend mode="multiply" in="SourceGraphic" in2="light" />
            <feColorMatrix
              type="matrix"
              values="0 0 0 0 0.25
                      0 0 0 0 0.23
                      0 0 0 0 0.35
                      0 0 0 0.3 0"
            />
          </filter>
          <rect width="100%" height="100%" filter="url(#rough-paper-lighting)" />
        </svg>
      )}

      {/* 4. WOVEN LINEN WEAVE */}
      {texture === 'linen' && (
        <div
          className="w-full h-full"
          style={{
            backgroundImage: `
              repeating-linear-gradient(0deg, rgba(51, 49, 78, 0.08) 0px, rgba(51, 49, 78, 0.08) 1px, transparent 1px, transparent 4px),
              repeating-linear-gradient(90deg, rgba(51, 49, 78, 0.08) 0px, rgba(51, 49, 78, 0.08) 1px, transparent 1px, transparent 4px)
            `,
          }}
        />
      )}

      {/* 5. TRADITIONAL LAID PAPER (Wirelines) */}
      {texture === 'laid' && (
        <div
          className="w-full h-full"
          style={{
            backgroundImage: `
              repeating-linear-gradient(180deg, rgba(51, 49, 78, 0.06) 0px, rgba(51, 49, 78, 0.06) 1px, transparent 1px, transparent 5px),
              repeating-linear-gradient(90deg, rgba(51, 49, 78, 0.09) 0px, rgba(51, 49, 78, 0.09) 1.5px, transparent 1.5px, transparent 38px)
            `,
          }}
        />
      )}
    </div>
  );
};
