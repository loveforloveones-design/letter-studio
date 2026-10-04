import React, { useState, useRef, useEffect } from 'react';
import { LetterData } from '../types';
import { LetterCanvas } from './LetterCanvas';
import { WaxSealBadge } from './WaxSeals';
import {
  Mail,
  MailOpen,
  Sparkles,
  Copy,
  Check,
  PenTool,
  Music,
  Play,
  Pause,
  Volume2,
  VolumeX,
  Disc,
} from 'lucide-react';

interface RecipientViewProps {
  letter: LetterData;
  onReply: (letter: LetterData) => void;
}

export const RecipientView: React.FC<RecipientViewProps> = ({
  letter,
  onReply,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [copied, setCopied] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMuted, setIsMuted] = useState(false);

  const audioRef = useRef<HTMLAudioElement | null>(null);

  // Auto-play Sahiba as soon as component mounts
  useEffect(() => {
    const playAudio = () => {
      if (audioRef.current) {
        audioRef.current
          .play()
          .then(() => setIsPlaying(true))
          .catch(() => {
            // If browser blocks unprompted autoplay, start on first user tap/click anywhere
            const handleFirstGesture = () => {
              if (audioRef.current) {
                audioRef.current
                  .play()
                  .then(() => setIsPlaying(true))
                  .catch(() => {});
              }
              window.removeEventListener('pointerdown', handleFirstGesture);
              window.removeEventListener('touchstart', handleFirstGesture);
              window.removeEventListener('click', handleFirstGesture);
              window.removeEventListener('keydown', handleFirstGesture);
            };

            window.addEventListener('pointerdown', handleFirstGesture, { once: true });
            window.addEventListener('touchstart', handleFirstGesture, { once: true });
            window.addEventListener('click', handleFirstGesture, { once: true });
            window.addEventListener('keydown', handleFirstGesture, { once: true });
          });
      }
    };

    playAudio();
  }, []);

  // Sync mute state with audio element
  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.muted = isMuted;
    }
  }, [isMuted]);

  // Handle open envelope and trigger Sahiba audio
  const handleOpenEnvelope = () => {
    setIsOpen(true);
    if (audioRef.current) {
      audioRef.current
        .play()
        .then(() => setIsPlaying(true))
        .catch((err) => {
          console.log('Audio autoplay prevented, user can toggle play:', err);
        });
    }
  };

  const togglePlayMusic = () => {
    if (!audioRef.current) return;
    if (isPlaying) {
      audioRef.current.pause();
      setIsPlaying(false);
    } else {
      audioRef.current
        .play()
        .then(() => setIsPlaying(true))
        .catch((err) => console.log('Audio play failed:', err));
    }
  };

  const toggleMute = () => {
    setIsMuted(!isMuted);
  };

  const handleCopyLink = async () => {
    try {
      await navigator.clipboard.writeText(window.location.href);
      setCopied(true);
      setTimeout(() => setCopied(false), 2500);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="min-h-screen flex flex-col font-urbanist text-[#33314e] selection:bg-[#ffd75e]">
      {/* Hidden Audio Element for Sahiba */}
      <audio
        ref={audioRef}
        loop
        preload="auto"
        onPlay={() => setIsPlaying(true)}
        onPause={() => setIsPlaying(false)}
      >
        <source src="/Sahiba.mp3" type="audio/mpeg" />
        <source src="/sahiba.mp3" type="audio/mpeg" />
        <source src="/sahiba.mp4" type="audio/mp4" />
        <source src="/Sahiba.mp4" type="audio/mp4" />
      </audio>

      {/* Top Banner for Recipient */}
      <header className="sticky top-0 z-40 bg-[#f8e2eb]/90 backdrop-blur-md border-b-2 border-[#33314e] px-3 sm:px-8 py-2.5 sm:py-3 flex items-center justify-between gap-2">
        <div className="flex items-center gap-2 sm:gap-2.5">
          <WaxSealBadge
            style={letter.sealStyle}
            color={letter.sealColor}
            size={32}
            monogramChar={letter.initialMonogram}
          />
          <div>
            <h1 className="text-base sm:text-xl font-black tracking-tight text-[#33314e]">
              Letter Studio
            </h1>
            <p className="text-[10px] text-[#7e7c97] font-semibold -mt-0.5 hidden xs:block">
              Handcrafted Sealed Letter
            </p>
          </div>
        </div>

        {/* Music Player Widget & Copy Link (NO Write/Create New Letter button) */}
        <div className="flex items-center gap-1.5 sm:gap-2.5">
          {/* Sahiba Audio Controller */}
          <div className="jp-btn px-2.5 sm:px-3 py-1 bg-[#fffdf8] text-[#33314e] text-xs font-bold flex items-center gap-1.5 shadow-xs">
            <Disc
              size={15}
              className={`text-[#f06e9a] ${isPlaying ? 'animate-spin-slow' : ''}`}
            />
            <span className="hidden sm:inline text-xs font-black tracking-tight">
              Sahiba
            </span>

            <button
              onClick={togglePlayMusic}
              title={isPlaying ? 'Pause Sahiba' : 'Play Sahiba'}
              className="p-1 hover:bg-[#ffd75e] rounded-full transition-colors cursor-pointer"
            >
              {isPlaying ? <Pause size={13} /> : <Play size={13} className="fill-current" />}
            </button>

            <button
              onClick={toggleMute}
              title={isMuted ? 'Unmute' : 'Mute'}
              className="p-1 hover:bg-[#ffd75e] rounded-full transition-colors cursor-pointer text-[#7e7c97] hover:text-[#33314e]"
            >
              {isMuted ? <VolumeX size={13} /> : <Volume2 size={13} />}
            </button>
          </div>

          <button
            onClick={handleCopyLink}
            className="jp-btn px-2.5 sm:px-3 py-1.5 bg-[#fffdf8] text-[#33314e] text-xs font-bold flex items-center gap-1.5 cursor-pointer whitespace-nowrap"
          >
            {copied ? <Check size={14} className="text-green-600" /> : <Copy size={14} />}
            <span className="hidden sm:inline">{copied ? 'Link Copied!' : 'Copy Link'}</span>
          </button>
        </div>
      </header>

      {/* Main Recipient Content */}
      <main className="flex-1 max-w-4xl w-full mx-auto p-4 sm:p-8 flex flex-col items-center justify-center">
        {/* Intro Notification */}
        <div className="text-center mb-6 max-w-md">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 bg-[#ffd75e] border border-[#33314e] rounded-full text-xs font-black uppercase tracking-wider text-[#33314e] mb-2 shadow-xs">
            <Sparkles size={12} className="text-[#f06e9a]" />
            <span>Special Delivery</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-extrabold text-[#33314e] tracking-tight">
            A letter for {letter.recipient || 'You'}
          </h2>
          <p className="text-xs sm:text-sm text-[#514e6e] mt-1">
            Sent with heartfelt care by{' '}
            <strong className="text-[#33314e] underline decoration-[#f06e9a] decoration-2">
              {letter.sender || 'Someone Special'}
            </strong>
          </p>

          {/* Sahiba Song Playing Pill Banner */}
          <div className="mt-3 inline-flex items-center gap-2 px-3 py-1 bg-[#fffdf8] border-2 border-[#33314e] rounded-full text-xs font-bold text-[#33314e] shadow-xs">
            <Music size={13} className="text-[#f06e9a]" />
            <span>Soundtrack: <strong>Sahiba</strong></span>
            <button
              onClick={togglePlayMusic}
              className="text-[#f06e9a] hover:underline font-extrabold ml-1 cursor-pointer flex items-center gap-1"
            >
              {isPlaying ? (
                <>
                  <Pause size={11} />
                  <span>Pause</span>
                </>
              ) : (
                <>
                  <Play size={11} className="fill-current" />
                  <span>Play song</span>
                </>
              )}
            </button>
          </div>
        </div>

        {!isOpen ? (
          /* Sealed Envelope Presentation View */
          <div className="flex flex-col items-center my-4 animate-fade-in">
            {/* The Envelope */}
            <div
              onClick={handleOpenEnvelope}
              className="group relative w-[320px] sm:w-[380px] aspect-[200/136] bg-[#fdf6df] border-2 border-[#33314e] rounded-2xl shadow-[6px_6px_0px_#33314e] cursor-pointer hover:-translate-y-1 transition-transform overflow-hidden"
            >
              {/* Back Flap Crease Lines */}
              <svg
                viewBox="0 0 200 136"
                className="absolute inset-0 w-full h-full pointer-events-none"
              >
                <path
                  d="M6 6 L100 82 L194 6"
                  fill="none"
                  stroke="#33314e"
                  strokeWidth="2.5"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
                <path
                  d="M6 130 L100 70 L194 130"
                  fill="none"
                  stroke="#33314e"
                  strokeWidth="1.2"
                  strokeDasharray="3 3"
                  opacity="0.3"
                />
              </svg>

              {/* Central Stamped Wax Seal */}
              <div className="absolute left-1/2 top-[52%] -translate-x-1/2 -translate-y-1/2 group-hover:scale-110 transition-transform duration-200">
                <WaxSealBadge
                  style={letter.sealStyle}
                  color={letter.sealColor}
                  size={76}
                  monogramChar={letter.initialMonogram}
                />
              </div>

              {/* Subtle Calligraphy Hint */}
              <div className="absolute bottom-3 left-4 text-[11px] font-handwriting text-[#514e6e]/80">
                To: {letter.recipient}
              </div>
            </div>

            {/* Prominent Open Button */}
            <button
              onClick={handleOpenEnvelope}
              className="mt-8 jp-btn px-8 py-3.5 bg-[#ffd75e] text-[#33314e] font-extrabold text-sm sm:text-base flex items-center gap-2.5 shadow-[4px_4px_0px_#33314e] hover:bg-[#ff9ec0] hover:scale-105 transition-all cursor-pointer"
            >
              <MailOpen size={18} />
              <span>Tap to Break Wax Seal & Open Letter</span>
            </button>
            <p className="text-xs text-[#7e7c97] mt-2">
              Tap the envelope or button to read with "Sahiba" music
            </p>
          </div>
        ) : (
          /* Opened Letter Reading View */
          <div className="w-full flex flex-col items-center space-y-6 animate-fade-in">
            {/* Quick Controls */}
            <div className="flex items-center gap-3">
              <button
                onClick={() => setIsOpen(false)}
                className="jp-btn px-4 py-1.5 bg-[#fffdf8] text-[#514e6e] text-xs font-bold flex items-center gap-1.5 cursor-pointer hover:text-[#33314e]"
              >
                <Mail size={14} />
                <span>Fold Back into Envelope</span>
              </button>

              <button
                onClick={togglePlayMusic}
                className="jp-btn px-3.5 py-1.5 bg-[#fffdf8] text-[#33314e] text-xs font-bold flex items-center gap-1.5 cursor-pointer hover:bg-[#ffd75e]"
              >
                {isPlaying ? <Pause size={13} /> : <Play size={13} className="fill-current" />}
                <span>{isPlaying ? 'Pause Sahiba' : 'Play Sahiba'}</span>
              </button>
            </div>

            {/* Document Canvas */}
            <div className="w-full max-w-[620px]">
              <LetterCanvas
                letter={letter}
                interactive={false}
              />
            </div>

            {/* Action Bar for the Recipient (Only Send a Reply - NO Create New Letter) */}
            <div className="jp-card p-6 w-full max-w-[620px] bg-[#fffdf8] flex flex-col sm:flex-row items-center justify-between gap-4">
              <div>
                <h4 className="font-extrabold text-base text-[#33314e]">
                  Loved this letter?
                </h4>
                <p className="text-xs text-[#7e7c97] mt-0.5">
                  Write a heartfelt reply note back to {letter.sender || 'your sender'}
                </p>
              </div>

              <div className="w-full sm:w-auto">
                <button
                  onClick={() => onReply(letter)}
                  className="w-full sm:w-auto jp-btn px-6 py-2.5 bg-[#f06e9a] text-white font-extrabold text-sm flex items-center justify-center gap-2 cursor-pointer hover:bg-[#e05b87]"
                >
                  <PenTool size={15} />
                  <span>Send a Reply</span>
                </button>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="border-t border-[#33314e]/20 py-5 px-4 text-center text-xs text-[#7e7c97] space-y-1.5">
        <div className="flex flex-wrap items-center justify-center gap-1.5 font-medium text-[#514e6e]">
          <span>Website design by</span>
          <span className="font-extrabold text-[#f06e9a] inline-flex items-center gap-1">
            love <span className="text-red-500">❤️</span>
          </span>
          <span className="text-[#33314e]/30 hidden xs:inline">·</span>
          <span>Contact:</span>
          <a
            href="mailto:loveforloveones@gmail.com"
            className="font-bold text-[#33314e] hover:text-[#f06e9a] underline decoration-[#ffd75e] decoration-2 transition-colors"
          >
            loveforloveones@gmail.com
          </a>
        </div>
        <p className="text-[11px] text-[#7e7c97]">
          Letter Studio · Handcrafted with wax seals & romantic music
        </p>
      </footer>
    </div>
  );
};
