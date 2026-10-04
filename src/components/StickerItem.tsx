import React, { useRef, useState } from 'react';
import { LetterSticker, StickerType } from '../types';
import { X, RotateCw } from 'lucide-react';

interface StickerItemProps {
  sticker: LetterSticker;
  onUpdate: (updated: Partial<LetterSticker>) => void;
  onRemove: () => void;
  interactive?: boolean;
}

export const StickerItem: React.FC<StickerItemProps> = ({
  sticker,
  onUpdate,
  onRemove,
  interactive = true,
}) => {
  const [isDragging, setIsDragging] = useState(false);
  const [isSelected, setIsSelected] = useState(false);
  const dragStartPos = useRef({ startX: 0, startY: 0, initialX: 0, initialY: 0 });

  const handlePointerDown = (e: React.PointerEvent) => {
    if (!interactive) return;
    e.stopPropagation();
    setIsDragging(true);
    setIsSelected(true);
    dragStartPos.current = {
      startX: e.clientX,
      startY: e.clientY,
      initialX: sticker.x,
      initialY: sticker.y,
    };
    (e.target as HTMLElement).setPointerCapture(e.pointerId);
  };

  const handlePointerMove = (e: React.PointerEvent) => {
    if (!isDragging || !interactive) return;
    const parent = (e.currentTarget as HTMLElement).parentElement;
    if (!parent) return;
    const rect = parent.getBoundingClientRect();
    const deltaX = ((e.clientX - dragStartPos.current.startX) / rect.width) * 100;
    const deltaY = ((e.clientY - dragStartPos.current.startY) / rect.height) * 100;

    const newX = Math.max(5, Math.min(95, dragStartPos.current.initialX + deltaX));
    const newY = Math.max(5, Math.min(95, dragStartPos.current.initialY + deltaY));
    onUpdate({ x: newX, y: newY });
  };

  const handlePointerUp = (e: React.PointerEvent) => {
    if (isDragging) {
      setIsDragging(false);
      try {
        (e.target as HTMLElement).releasePointerCapture(e.pointerId);
      } catch (err) {}
    }
  };

  return (
    <div
      className={`absolute select-none transition-shadow ${
        interactive ? 'cursor-grab active:cursor-grabbing' : 'pointer-events-none'
      }`}
      style={{
        left: `${sticker.x}%`,
        top: `${sticker.y}%`,
        transform: `translate(-50%, -50%) rotate(${sticker.rotation}deg) scale(${sticker.scale})`,
        zIndex: isSelected ? 30 : 20,
      }}
      onPointerDown={handlePointerDown}
      onPointerMove={handlePointerMove}
      onPointerUp={handlePointerUp}
      onClick={() => interactive && setIsSelected(!isSelected)}
    >
      <div
        className={`relative group ${
          isSelected && interactive ? 'ring-2 ring-[#f06e9a] ring-offset-2 rounded-lg' : ''
        }`}
      >
        {renderStickerGraphic(sticker.type)}

        {/* Controls when selected */}
        {isSelected && interactive && (
          <div className="absolute -top-3 -right-3 flex items-center gap-1 z-40">
            <button
              onClick={(e) => {
                e.stopPropagation();
                onUpdate({ rotation: (sticker.rotation + 15) % 360 });
              }}
              title="Rotate"
              className="w-5 h-5 bg-[#ffd75e] text-[#33314e] border border-[#33314e] rounded-full flex items-center justify-center hover:scale-110 shadow-sm cursor-pointer"
            >
              <RotateCw size={10} />
            </button>
            <button
              onClick={(e) => {
                e.stopPropagation();
                onRemove();
              }}
              title="Remove"
              className="w-5 h-5 bg-[#33314e] text-white rounded-full flex items-center justify-center hover:bg-red-600 transition-colors shadow-sm cursor-pointer"
            >
              <X size={12} />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

function renderStickerGraphic(type: StickerType) {
  switch (type) {
    case 'stamp':
      return (
        <div className="relative w-16 h-20 bg-[#fdf6df] border-2 border-[#33314e] rounded p-1 shadow-sm flex flex-col items-center justify-between">
          {/* Perforated border look */}
          <div className="w-full h-full border border-dashed border-[#33314e] bg-[#f8e2eb] flex flex-col items-center justify-center p-1">
            <div className="w-6 h-6 rounded-full bg-[#ff9ec0] border border-[#33314e] flex items-center justify-center text-[10px]">
              🕊️
            </div>
            <span className="text-[8px] font-bold text-[#33314e] mt-1 tracking-widest uppercase">
              POST
            </span>
          </div>
          {/* Postmark cancellation lines */}
          <svg
            className="absolute -right-3 top-2 w-10 h-6 text-[#33314e] opacity-70 pointer-events-none"
            viewBox="0 0 40 24"
            fill="none"
          >
            <path d="M0 6 Q 10 0, 20 6 T 40 6" stroke="currentColor" strokeWidth="1.5" />
            <path d="M0 14 Q 10 8, 20 14 T 40 14" stroke="currentColor" strokeWidth="1.5" />
          </svg>
        </div>
      );

    case 'bow':
      return (
        <svg width="48" height="42" viewBox="0 0 48 42" fill="none" className="filter drop-shadow-sm">
          <g stroke="#33314e" strokeWidth="2.2" strokeLinejoin="round">
            <path d="M24 20 C14 8, 2 12, 6 22 C10 28, 20 24, 24 20 Z" fill="#ff9ec0" />
            <path d="M24 20 C34 8, 46 12, 42 22 C38 28, 28 24, 24 20 Z" fill="#ff9ec0" />
            <path d="M20 22 L12 38 L18 36 L23 25" fill="#f06e9a" />
            <path d="M28 22 L36 38 L30 36 L25 25" fill="#f06e9a" />
            <circle cx="24" cy="20" r="4.5" fill="#ffd75e" />
          </g>
        </svg>
      );

    case 'lily':
      return (
        <svg width="44" height="44" viewBox="0 0 44 44" fill="none" className="filter drop-shadow-sm">
          <g stroke="#33314e" strokeWidth="2">
            {[0, 72, 144, 216, 288].map((angle, i) => (
              <path
                key={i}
                d="M22 22 C18 14, 16 6, 22 4 C28 6, 26 14, 22 22 Z"
                fill="#fffdf8"
                transform={`rotate(${angle} 22 22)`}
              />
            ))}
            <circle cx="22" cy="22" r="4.5" fill="#ffd75e" />
          </g>
        </svg>
      );

    case 'heart':
      return (
        <svg width="40" height="38" viewBox="0 0 40 38" fill="none" className="filter drop-shadow-sm">
          <path
            d="M20 34 C20 34 4 23 4 12 C4 5 10 2 15 5 C18 7 20 11 20 11 C20 11 22 7 25 5 C30 2 36 5 36 12 C36 23 20 34 20 34 Z"
            fill="#c1392b"
            stroke="#33314e"
            strokeWidth="2.2"
            strokeLinejoin="round"
          />
        </svg>
      );

    case 'coffee':
      return (
        <div className="relative text-2xl filter drop-shadow-sm select-none p-1 bg-[#fffdf8] border-2 border-[#33314e] rounded-xl">
          ☕
        </div>
      );

    case 'star':
      return (
        <svg width="38" height="38" viewBox="0 0 38 38" fill="none" className="filter drop-shadow-sm">
          <path
            d="M19 4 L23 14 L34 15 L26 23 L28 34 L19 28 L10 34 L12 23 L4 15 L15 14 Z"
            fill="#ffd75e"
            stroke="#33314e"
            strokeWidth="2.2"
            strokeLinejoin="round"
          />
        </svg>
      );

    case 'teddy':
      return (
        <svg width="46" height="42" viewBox="0 0 46 42" fill="none" className="filter drop-shadow-sm">
          <g stroke="#33314e" strokeWidth="2.2" strokeLinejoin="round">
            <circle cx="12" cy="12" r="6" fill="#a9744f" />
            <circle cx="34" cy="12" r="6" fill="#a9744f" />
            <circle cx="23" cy="24" r="14" fill="#d1986e" />
            <circle cx="18" cy="22" r="1.8" fill="#33314e" />
            <circle cx="28" cy="22" r="1.8" fill="#33314e" />
            <ellipse cx="23" cy="27" rx="5" ry="3.8" fill="#fffdf8" />
            <circle cx="23" cy="26" r="1.5" fill="#33314e" />
          </g>
        </svg>
      );

    case 'botanical':
      return (
        <div className="text-2xl filter drop-shadow-sm select-none">
          🌿
        </div>
      );

    case 'washi':
      return (
        <div className="w-16 h-5 bg-[#ffd75e]/90 border border-[#33314e]/30 shadow-sm transform -rotate-3" />
      );

    default:
      return null;
  }
}
