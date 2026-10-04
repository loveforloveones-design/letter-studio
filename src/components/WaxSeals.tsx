import React from 'react';
import { SealColor, SealStyle } from '../types';

export const SEAL_COLOR_MAP: Record<
  SealColor,
  { name: string; bg: string; darkRim: string; highlight: string }
> = {
  crimson: {
    name: 'Crimson Red',
    bg: '#c1392b',
    darkRim: '#872419',
    highlight: '#e74c3c',
  },
  petal_rose: {
    name: 'Petal Rose',
    bg: '#f4a4c0',
    darkRim: '#b86682',
    highlight: '#ffd1e0',
  },
  teddy_caramel: {
    name: 'Teddy Caramel',
    bg: '#a9744f',
    darkRim: '#6e462b',
    highlight: '#d1986e',
  },
  royal_navy: {
    name: 'Royal Navy',
    bg: '#2b3a67',
    darkRim: '#18223f',
    highlight: '#485f9e',
  },
  sage_olive: {
    name: 'Sage Olive',
    bg: '#557b55',
    darkRim: '#355335',
    highlight: '#78a678',
  },
  antique_gold: {
    name: 'Antique Gold',
    bg: '#d4a340',
    darkRim: '#926a1b',
    highlight: '#f7c768',
  },
  midnight_ink: {
    name: 'Midnight Ink',
    bg: '#33314e',
    darkRim: '#1b1a2e',
    highlight: '#57547d',
  },
};

interface WaxSealBadgeProps {
  style: SealStyle;
  color: SealColor;
  size?: number;
  monogramChar?: string;
  className?: string;
}

export const WaxSealBadge: React.FC<WaxSealBadgeProps> = ({
  style,
  color,
  size = 56,
  monogramChar = 'S',
  className = '',
}) => {
  const palette = SEAL_COLOR_MAP[color] || SEAL_COLOR_MAP.crimson;
  const initial = (monogramChar || 'S').trim().toUpperCase().charAt(0) || 'S';

  return (
    <div
      className={`inline-flex items-center justify-center shrink-0 select-none ${className}`}
      style={{ width: size, height: size }}
    >
      <svg
        width={size}
        height={size}
        viewBox="0 0 100 100"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className="drop-shadow-sm filter"
      >
        {/* Irregular molten wax puddle background */}
        <path
          d="M50 6 C66 4, 82 12, 88 28 C94 44, 98 62, 86 78 C74 94, 52 96, 36 94 C20 92, 4 82, 6 64 C8 46, 6 28, 22 14 C32 4, 42 7, 50 6 Z"
          fill={palette.bg}
          stroke="#33314e"
          strokeWidth="3.5"
          strokeLinejoin="round"
        />

        {/* Inner debossed groove */}
        <circle
          cx="50"
          cy="50"
          r="36"
          stroke={palette.darkRim}
          strokeWidth="3"
          strokeDasharray="2 1"
          opacity="0.8"
        />

        {/* Glossy wax highlight curved reflection */}
        <path
          d="M26 34 C30 24, 42 18, 56 18"
          stroke={palette.highlight}
          strokeWidth="3.5"
          strokeLinecap="round"
          opacity="0.65"
        />

        {/* Stamped Emblem */}
        {style === 'heart' && (
          <path
            d="M50 72 C50 72 26 56 26 38 C26 26 36 22 43 27 C48 31 50 36 50 36 C50 36 52 31 57 27 C64 22 74 26 74 38 C74 56 50 72 50 72 Z"
            fill={palette.darkRim}
            stroke="#33314e"
            strokeWidth="2.8"
            strokeLinejoin="round"
          />
        )}

        {style === 'bow' && (
          <g stroke="#33314e" strokeWidth="2.4" strokeLinejoin="round">
            {/* Left loop */}
            <path
              d="M50 48 C40 32, 24 38, 28 50 C32 58, 44 54, 50 48 Z"
              fill={palette.highlight}
            />
            {/* Right loop */}
            <path
              d="M50 48 C60 32, 76 38, 72 50 C68 58, 56 54, 50 48 Z"
              fill={palette.highlight}
            />
            {/* Left tail */}
            <path d="M46 52 L36 74 L42 72 L49 55" fill={palette.darkRim} />
            {/* Right tail */}
            <path d="M54 52 L64 74 L58 72 L51 55" fill={palette.darkRim} />
            {/* Center knot */}
            <circle cx="50" cy="48" r="5" fill="#f06e9a" />
          </g>
        )}

        {style === 'lily' && (
          <g stroke="#33314e" strokeWidth="2.2" strokeLinejoin="round">
            {/* 5 Petals */}
            {[0, 72, 144, 216, 288].map((angle, i) => (
              <path
                key={i}
                d="M50 50 C46 36, 42 22, 50 18 C58 22, 54 36, 50 50 Z"
                fill="#fffdf8"
                transform={`rotate(${angle} 50 50)`}
              />
            ))}
            {/* Yellow Center */}
            <circle cx="50" cy="50" r="6" fill="#ffd75e" />
          </g>
        )}

        {style === 'teddy' && (
          <g stroke="#33314e" strokeWidth="2.4" strokeLinejoin="round">
            {/* Ears */}
            <circle cx="34" cy="34" r="9" fill={palette.darkRim} />
            <circle cx="66" cy="34" r="9" fill={palette.darkRim} />
            {/* Head */}
            <circle cx="50" cy="52" r="22" fill={palette.bg} />
            {/* Eyes */}
            <circle cx="42" cy="48" r="2.8" fill="#33314e" />
            <circle cx="58" cy="48" r="2.8" fill="#33314e" />
            {/* Muzzle */}
            <ellipse cx="50" cy="58" rx="8" ry="6" fill="#fffdf8" />
            <circle cx="50" cy="56" r="2.5" fill="#33314e" />
          </g>
        )}

        {style === 'star' && (
          <path
            d="M50 20 L58 38 L78 39 L62 52 L68 72 L50 60 L32 72 L38 52 L22 39 L42 38 Z"
            fill="#ffd75e"
            stroke="#33314e"
            strokeWidth="2.6"
            strokeLinejoin="round"
          />
        )}

        {style === 'rose_crest' && (
          <g stroke="#33314e" strokeWidth="2.2" strokeLinecap="round">
            {/* Laurel wreath leaves */}
            <path
              d="M32 70 C24 55, 24 38, 44 26"
              fill="none"
              strokeWidth="2.6"
            />
            <path
              d="M68 70 C76 55, 76 38, 56 26"
              fill="none"
              strokeWidth="2.6"
            />
            {/* Center Rose Petals */}
            <circle cx="50" cy="48" r="10" fill={palette.darkRim} />
            <circle cx="50" cy="48" r="5" fill="#fffdf8" />
          </g>
        )}

        {style === 'monogram' && (
          <g>
            <circle
              cx="50"
              cy="50"
              r="26"
              stroke="#33314e"
              strokeWidth="2.2"
              fill="none"
            />
            <text
              x="50"
              y="60"
              textAnchor="middle"
              fill="#33314e"
              fontSize="28"
              fontWeight="bold"
              fontFamily="Playfair Display, serif"
            >
              {initial}
            </text>
          </g>
        )}
      </svg>
    </div>
  );
};
