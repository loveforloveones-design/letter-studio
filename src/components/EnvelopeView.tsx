import React, { useState } from 'react';
import { LetterData, SealStyle, SealColor } from '../types';
import { WaxSealBadge, SEAL_COLOR_MAP } from './WaxSeals';
import { Mail, MailOpen, RefreshCw, Stamp } from 'lucide-react';

interface EnvelopeViewProps {
  letter: LetterData;
  onOpenLetter: () => void;
  onUpdateLetter?: (fields: Partial<LetterData>) => void;
}

export const EnvelopeView: React.FC<EnvelopeViewProps> = ({
  letter,
  onOpenLetter,
  onUpdateLetter,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [showFlapSide, setShowFlapSide] = useState(true);

  return (
    <div className="w-full flex flex-col items-center py-2 space-y-6">
      <div className="text-center">
        <h2 className="text-xl sm:text-2xl font-extrabold text-[#33314e] font-urbanist">
          {isOpen ? 'Your Letter is Unsealed!' : 'Sealed Envelope Presentation'}
        </h2>
        <p className="text-xs sm:text-sm text-[#514e6e] mt-1">
          {isOpen
            ? 'The envelope has opened — view or edit your document'
            : 'Tap the wax seal or button below to test unsealing'}
        </p>
      </div>

      {/* Envelope Stage */}
      <div className="relative w-full max-w-[380px] h-[240px] flex items-center justify-center my-2">
        {/* Letter paper sliding out behind pocket */}
        <div
          onClick={onOpenLetter}
          className={`absolute w-[300px] h-[180px] rounded-xl border-2 border-[#33314e] bg-[#fffdf8] p-4 shadow-md transition-all duration-700 ease-out cursor-pointer ${
            isOpen ? '-translate-y-24 opacity-100' : 'translate-y-0 opacity-40 scale-95'
          }`}
          style={{ zIndex: isOpen ? 10 : 2 }}
        >
          <div className="border-b border-[#33314e]/20 pb-1 mb-2 flex justify-between items-center text-[10px] text-[#7e7c97]">
            <span className="font-bold uppercase tracking-wider text-[#f06e9a]">
              LETTERHEAD
            </span>
            <span>{letter.date}</span>
          </div>
          <p className="font-bold text-xs text-[#33314e] mb-1">
            {letter.salutation || `Dear ${letter.recipient},`}
          </p>
          <p className="font-handwriting text-xs text-[#514e6e] line-clamp-4 leading-relaxed">
            {letter.bodyText}
          </p>
        </div>

        {/* Envelope Pocket */}
        <div
          onClick={() => setIsOpen(!isOpen)}
          className="relative z-20 w-[330px] aspect-[200/136] bg-[#fdf6df] border-2 border-[#33314e] rounded-2xl shadow-[4px_4px_0px_#33314e] overflow-hidden cursor-pointer"
        >
          {showFlapSide ? (
            /* Back side with flap folds and central wax seal */
            <div className="relative w-full h-full">
              <svg
                viewBox="0 0 200 136"
                className="absolute inset-0 w-full h-full pointer-events-none"
              >
                {/* Flap top diagonals */}
                <path
                  d="M6 6 L100 82 L194 6"
                  fill="none"
                  stroke="#33314e"
                  strokeWidth="2.5"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
                {/* Bottom pocket subtle fold */}
                <path
                  d="M6 130 L100 70 L194 130"
                  fill="none"
                  stroke="#33314e"
                  strokeWidth="1.2"
                  strokeDasharray="3 3"
                  opacity="0.3"
                />
              </svg>

              {/* Centered Wax Seal at intersection of flap */}
              <div
                className={`absolute left-1/2 top-[52%] -translate-x-1/2 -translate-y-1/2 transition-transform duration-300 ${
                  isOpen ? 'scale-90 opacity-90' : 'hover:scale-105'
                }`}
              >
                <WaxSealBadge
                  style={letter.sealStyle}
                  color={letter.sealColor}
                  size={68}
                  monogramChar={letter.initialMonogram}
                />
              </div>
            </div>
          ) : (
            /* Front of envelope with recipient name calligraphy and postage stamp */
            <div className="relative w-full h-full p-4 flex flex-col justify-between">
              {/* Postage Stamp top right */}
              <div className="self-end w-12 h-14 bg-[#f8e2eb] border border-[#33314e] rounded p-1 flex flex-col items-center justify-center -rotate-3 shadow-sm">
                <span className="text-sm">📮</span>
                <span className="text-[7px] font-bold text-[#33314e] uppercase tracking-wider mt-0.5">
                  AIR MAIL
                </span>
              </div>

              {/* Centered Calligraphy: "for Sophie" */}
              <div className="text-center my-auto pb-4">
                <p className="text-xs italic text-[#514e6e] font-serif-classic">for</p>
                <h3 className="text-2xl sm:text-3xl font-handwriting font-bold text-[#33314e] mt-0.5">
                  {letter.recipient || 'Someone Special'}
                </h3>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Control Buttons */}
      <div className="flex items-center gap-2.5">
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="jp-btn px-5 py-2 bg-[#ffd75e] text-[#33314e] font-bold flex items-center gap-2 text-xs sm:text-sm cursor-pointer"
        >
          {isOpen ? <Mail size={15} /> : <MailOpen size={15} />}
          <span>{isOpen ? 'Seal Envelope' : 'Test Open Envelope'}</span>
        </button>

        <button
          onClick={() => setShowFlapSide(!showFlapSide)}
          className="jp-btn px-4 py-2 bg-[#fffdf8] text-[#33314e] font-bold flex items-center gap-2 text-xs sm:text-sm cursor-pointer"
        >
          <RefreshCw size={14} />
          <span>{showFlapSide ? 'Flip to Address' : 'Flip to Seal'}</span>
        </button>
      </div>

      {/* Wax Seal Customization Section */}
      {onUpdateLetter && (
        <div className="w-full space-y-4 pt-4 border-t border-[#33314e]/20">
          <div className="space-y-2">
            <h4 className="font-extrabold text-sm text-[#33314e] flex items-center gap-1.5">
              <Stamp size={15} className="text-[#f06e9a]" />
              <span>Wax Seal Emblem</span>
            </h4>
            <div className="grid grid-cols-4 sm:grid-cols-7 gap-2">
              {[
                { id: 'heart', label: 'Heart' },
                { id: 'bow', label: 'Bow' },
                { id: 'lily', label: 'Lily' },
                { id: 'teddy', label: 'Teddy' },
                { id: 'star', label: 'Star' },
                { id: 'rose_crest', label: 'Crest' },
                { id: 'monogram', label: 'Monogram' },
              ].map((s) => (
                <button
                  key={s.id}
                  onClick={() => onUpdateLetter({ sealStyle: s.id as SealStyle })}
                  className={`py-2 px-1 rounded-xl border-2 flex flex-col items-center justify-center cursor-pointer transition-transform ${
                    letter.sealStyle === s.id
                      ? 'bg-[#ffd75e] border-[#33314e] scale-105 shadow-sm'
                      : 'bg-[#fffdf8] border-[#33314e]/30 hover:border-[#33314e]'
                  }`}
                >
                  <WaxSealBadge
                    style={s.id as SealStyle}
                    color={letter.sealColor}
                    size={32}
                    monogramChar={letter.initialMonogram}
                  />
                  <span className="text-[10px] font-bold text-[#33314e] mt-1 text-center">
                    {s.label}
                  </span>
                </button>
              ))}
            </div>
          </div>

          {/* Wax Color Picker */}
          <div className="space-y-2">
            <h4 className="font-extrabold text-sm text-[#33314e]">Wax Color</h4>
            <div className="flex flex-wrap gap-2">
              {Object.entries(SEAL_COLOR_MAP).map(([key, item]) => (
                <button
                  key={key}
                  onClick={() => onUpdateLetter({ sealColor: key as SealColor })}
                  className={`px-3 py-1.5 rounded-xl border-2 text-xs font-bold flex items-center gap-2 cursor-pointer ${
                    letter.sealColor === key
                      ? 'border-[#33314e] ring-2 ring-[#f06e9a] shadow-sm'
                      : 'border-[#33314e]/30'
                  }`}
                  style={{ backgroundColor: item.bg, color: '#ffffff' }}
                >
                  <span
                    className="w-2.5 h-2.5 rounded-full border border-black/30"
                    style={{ backgroundColor: item.bg }}
                  />
                  <span>{item.name}</span>
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
