import React, { useRef, useState, useEffect } from 'react';
import {
  FontChoice,
  HeaderStyle,
  LetterData,
  PaperTheme,
  PaperTexture,
  SealColor,
  SealStyle,
  StickerType,
} from './types';
import { LetterCanvas } from './components/LetterCanvas';
import { EnvelopeView } from './components/EnvelopeView';
import { WaxSealBadge, SEAL_COLOR_MAP } from './components/WaxSeals';
import { RecipientView } from './components/RecipientView';
import { SendSharePanel } from './components/SendSharePanel';
import { getInitialLetterFromUrl } from './utils/letterLink';
import {
  FileText,
  Palette,
  Stamp,
  Sparkles,
  Mail,
  Send,
  Eye,
  Sliders,
  Check,
  RotateCcw,
  Sparkle,
  PenTool,
  ArrowRight,
} from 'lucide-react';

const INITIAL_LETTER: LetterData = {
  recipient: 'Sophie',
  recipientEmail: 'sophie@atelierdelumiere.com',
  salutation: 'My dearest Sophie,',

  sender: 'Sabi',
  senderTitle: 'Creative Director',
  senderOrg: 'Janapa Studio',
  senderAddress: '44 Wisteria Walk, Studio 3B',
  senderEmail: 'loveforloveones@gmail.com',
  senderPhone: '+1 (555) 392-8104',
  senderWebsite: 'letter.janapasabi.com',
  referenceNumber: 'JS/2026/089-A',
  date: 'October 4, 2026',

  subject: 'Some words deserve to be written down',
  bodyText: `I wanted to send you something you could hold in your hands.

In a world of fleeting notifications, take a breath and know how deeply appreciated you are. Every idea we spoke about has begun to flourish, and every sunrise feels a little warmer remembering our shared laughter.

May this little letter find you surrounded by quiet peace, a warm cup in hand, and the gentle reminder that great things take time to unfold.`,

  signOff: 'With all my love and fondness,',
  signatureText: 'Sabi',
  initialMonogram: 'S',

  headerStyle: 'none',
  paperTheme: 'cream_laid',
  paperTexture: 'grain',
  textureIntensity: 'medium',
  fontChoice: 'handwritten',
  fontSize: 16,
  sealStyle: 'heart',
  sealColor: 'crimson',
  showRuledLines: true,

  stickers: [
    {
      id: 'stamp-1',
      type: 'stamp',
      x: 88,
      y: 10,
      rotation: -4,
      scale: 1.0,
    },
    {
      id: 'lily-1',
      type: 'lily',
      x: 12,
      y: 92,
      rotation: 8,
      scale: 1.1,
    },
  ],

  hasPolaroid: false,
  polaroidCaption: 'autumn in the park',
  polaroidImage: '',
  polaroidRotation: -3,
};

type ActiveTab = 'write' | 'style' | 'envelope' | 'send';

export default function App() {
  const [viewMode, setViewMode] = useState<'create' | 'recipient'>('create');
  const [letter, setLetter] = useState<LetterData>(INITIAL_LETTER);
  const [activeTab, setActiveTab] = useState<ActiveTab>('write');
  const [showTemplateModal, setShowTemplateModal] = useState(false);
  const [mobilePreviewModal, setMobilePreviewModal] = useState(false);

  const canvasRef = useRef<HTMLDivElement | null>(null);

  // Check URL on load to see if opened from a receiver's independent link
  useEffect(() => {
    const urlLetter = getInitialLetterFromUrl();
    if (urlLetter) {
      setLetter(urlLetter);
      setViewMode('recipient');
    }
  }, []);

  const updateLetter = (fields: Partial<LetterData>) => {
    setLetter((prev) => ({ ...prev, ...fields }));
  };

  const handleRecipientReply = (receivedLetter: LetterData) => {
    // Pre-fill a reply from the recipient back to original sender
    setLetter({
      ...INITIAL_LETTER,
      recipient: receivedLetter.sender,
      recipientEmail: receivedLetter.senderEmail,
      sender: receivedLetter.recipient,
      senderEmail: receivedLetter.recipientEmail,
      salutation: `Dear ${receivedLetter.sender},`,
      subject: `Re: ${receivedLetter.subject}`,
      bodyText: `Thank you so much for your thoughtful letter. Reading your words brought such a smile to my day.\n\n`,
      signOff: 'Fondly,',
      signatureText: receivedLetter.recipient,
      paperTheme: receivedLetter.paperTheme,
      paperTexture: receivedLetter.paperTexture,
      sealStyle: 'heart',
      sealColor: 'crimson',
    });
    setViewMode('create');
    setActiveTab('write');
  };

  const handleCreateNew = () => {
    setLetter(INITIAL_LETTER);
    // Clear URL parameters
    if (window.history.pushState) {
      const cleanUrl = window.location.protocol + '//' + window.location.host + window.location.pathname;
      window.history.pushState({ path: cleanUrl }, '', cleanUrl);
    }
    setViewMode('create');
    setActiveTab('write');
  };

  // 1. IF IN RECIPIENT VIEW MODE: Show independent letter unsealing experience
  if (viewMode === 'recipient') {
    return (
      <RecipientView
        letter={letter}
        onReply={handleRecipientReply}
      />
    );
  }

  // 2. CREATOR / SENDER WORKSPACE
  return (
    <div className="min-h-screen flex flex-col font-urbanist text-[#33314e] selection:bg-[#ffd75e]">
      {/* 1. TOP BAR */}
      <header className="sticky top-0 z-40 bg-[#f8e2eb]/90 backdrop-blur-md border-b-2 border-[#33314e] px-3 sm:px-6 lg:px-8 py-2.5 sm:py-3.5 flex items-center justify-between gap-2">
        {/* Brand Zone */}
        <div className="flex items-center gap-2 sm:gap-2.5 shrink-0">
          <WaxSealBadge
            style={letter.sealStyle}
            color={letter.sealColor}
            size={32}
            monogramChar={letter.initialMonogram}
          />
          <div>
            <h1 className="text-base sm:text-xl font-black tracking-tight text-[#33314e] whitespace-nowrap">
              Letter Studio
            </h1>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-1.5 sm:gap-2.5 shrink-0">
          <button
            onClick={() => setShowTemplateModal(true)}
            title="Templates"
            className="jp-btn px-2.5 sm:px-3 py-1.5 bg-[#fffdf8] text-[#33314e] text-xs sm:text-sm flex items-center gap-1.5 cursor-pointer whitespace-nowrap shrink-0"
          >
            <Sparkles size={14} className="text-[#f06e9a] shrink-0" />
            <span className="hidden md:inline">Templates</span>
          </button>

          <button
            onClick={() => setViewMode('recipient')}
            title="Preview how the receiver will see and unseal this letter"
            className="jp-btn px-2.5 sm:px-3 py-1.5 bg-[#fffdf8] text-[#33314e] text-xs sm:text-sm flex items-center gap-1.5 cursor-pointer hover:bg-[#ffd75e] whitespace-nowrap shrink-0"
          >
            <Eye size={14} className="shrink-0" />
            <span className="hidden md:inline">Preview</span>
          </button>

          {/* Primary Action: Go to Send & Share */}
          <button
            onClick={() => setActiveTab('send')}
            className="jp-btn px-3 sm:px-4 py-1.5 bg-[#ffd75e] text-[#33314e] text-xs sm:text-sm font-extrabold flex items-center gap-1.5 cursor-pointer hover:bg-[#ff9ec0] whitespace-nowrap shrink-0"
          >
            <Send size={14} className="shrink-0" />
            <span className="hidden md:inline">Send & Share Letter</span>
            <span className="hidden sm:inline md:hidden">Send & Share</span>
            <span className="sm:hidden">Send</span>
          </button>

          {/* Mobile Preview Toggle */}
          <button
            onClick={() => setMobilePreviewModal(true)}
            title="View letter sheet"
            className="lg:hidden jp-btn px-2.5 sm:px-3 py-1.5 bg-[#ff9ec0] text-[#33314e] text-xs font-bold flex items-center gap-1 cursor-pointer whitespace-nowrap shrink-0"
          >
            <Eye size={14} className="shrink-0" />
            <span className="hidden sm:inline">Sheet</span>
          </button>
        </div>
      </header>

      {/* 2. MAIN WORKSPACE */}
      <main className="flex-1 max-w-[1440px] w-full mx-auto p-4 sm:p-6 lg:p-8 flex flex-col lg:flex-row gap-8 items-start">
        {/* LEFT COLUMN: Toolbars & Customization Panels */}
        <div className="w-full lg:w-[480px] xl:w-[520px] shrink-0 flex flex-col gap-4">
          {/* Navigation Tabs */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-2 scrollbar-none">
            {[
              { id: 'write', label: 'Write', icon: FileText },
              { id: 'style', label: 'Letterhead', icon: Palette },
              { id: 'envelope', label: 'Envelope & Seal', icon: Mail },
              { id: 'send', label: 'Send & Share', icon: Send },
            ].map((tab) => {
              const IconComp = tab.icon;
              const isSelected = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as ActiveTab)}
                  className={`jp-btn px-3 sm:px-3.5 py-2 text-xs font-bold whitespace-nowrap shrink-0 flex items-center gap-1.5 cursor-pointer ${
                    isSelected ? 'bg-[#ffd75e] text-[#33314e]' : 'bg-[#fffdf8] text-[#514e6e]'
                  }`}
                >
                  <IconComp size={14} />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>

          {/* TAB 1: WRITE & CONTENT */}
          {activeTab === 'write' && (
            <div className="space-y-4">
              {/* Recipient Info (without address) */}
              <div className="jp-card p-5 space-y-3">
                <h3 className="font-extrabold text-base text-[#33314e]">Who's this letter for?</h3>
                <div>
                  <label className="text-xs font-bold text-[#514e6e] block mb-1">
                    Recipient Name
                  </label>
                  <input
                    type="text"
                    value={letter.recipient}
                    onChange={(e) => updateLetter({ recipient: e.target.value })}
                    placeholder="e.g. Sophie"
                    className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e] focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
                  />
                </div>

                <div>
                  <div className="flex items-center justify-between mb-1">
                    <label className="text-xs font-bold text-[#514e6e] block">
                      Receiver's Email Address
                    </label>
                    <span className="text-[10px] text-[#f06e9a] font-bold">For direct delivery</span>
                  </div>
                  <input
                    type="email"
                    value={letter.recipientEmail}
                    onChange={(e) => updateLetter({ recipientEmail: e.target.value })}
                    placeholder="e.g. sophie@atelierdelumiere.com"
                    className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e] focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
                  />
                </div>

                <div>
                  <label className="text-xs font-bold text-[#514e6e] block mb-1">
                    Greeting / Salutation
                  </label>
                  <input
                    type="text"
                    value={letter.salutation}
                    onChange={(e) => updateLetter({ salutation: e.target.value })}
                    placeholder="My dearest Sophie,"
                    className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e] focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
                  />
                </div>
              </div>

              {/* Subject & Letter Body */}
              <div className="jp-card p-5 space-y-3">
                <div className="flex items-center justify-between">
                  <h3 className="font-extrabold text-base text-[#33314e]">Letter Body</h3>
                  <span className="text-xs text-[#7e7c97]">
                    {letter.bodyText.length} characters
                  </span>
                </div>

                <div>
                  <label className="text-xs font-bold text-[#514e6e] block mb-1">
                    Subject Line (Optional)
                  </label>
                  <input
                    type="text"
                    value={letter.subject}
                    onChange={(e) => updateLetter({ subject: e.target.value })}
                    placeholder="e.g. Some words deserve to be written down"
                    className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e] focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
                  />
                </div>

                <div>
                  <label className="text-xs font-bold text-[#514e6e] block mb-1">
                    Message Content
                  </label>
                  <textarea
                    rows={8}
                    value={letter.bodyText}
                    onChange={(e) => updateLetter({ bodyText: e.target.value })}
                    placeholder="Write your heartfelt note or letterhead correspondence..."
                    className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl p-3.5 text-sm text-[#33314e] leading-relaxed focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
                  />
                </div>
              </div>

              {/* Sign-off & Handwritten Signature */}
              <div className="jp-card p-5 space-y-3">
                <h3 className="font-extrabold text-base text-[#33314e]">Sign-off & Signature</h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">
                      Closing Valediction
                    </label>
                    <input
                      type="text"
                      value={letter.signOff}
                      onChange={(e) => updateLetter({ signOff: e.target.value })}
                      placeholder="With all my love,"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e] focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">
                      Signature Pen Name
                    </label>
                    <input
                      type="text"
                      value={letter.signatureText}
                      onChange={(e) => updateLetter({ signatureText: e.target.value })}
                      placeholder="Sabi"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e] focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
                    />
                  </div>
                </div>
              </div>

              <button
                onClick={() => setActiveTab('style')}
                className="w-full jp-btn py-3 bg-[#f06e9a] text-white font-bold text-sm flex items-center justify-center gap-2 cursor-pointer hover:bg-[#e05b87]"
              >
                <span>Continue to Letterhead & Style →</span>
              </button>
            </div>
          )}

          {/* TAB 2: LETTERHEAD & STYLE */}
          {activeTab === 'style' && (
            <div className="space-y-4">
              {/* Header Style Selector */}
              <div className="jp-card p-5 space-y-3">
                <h3 className="font-extrabold text-base text-[#33314e]">Letterhead Layout</h3>
                <div className="grid grid-cols-1 gap-2">
                  {[
                    {
                      id: 'none',
                      name: '📄 Clean / No Header',
                      desc: 'Pure stationery letter without any top header banner',
                    },
                    {
                      id: 'corporate_official',
                      name: '🏢 Executive Letterhead',
                      desc: 'Formal corporate banner, 2-column contact grid, ref number',
                    },
                    {
                      id: 'minimal_modern',
                      name: '⬛ Minimalist Monogram',
                      desc: 'Crisp initial badge, sleek title, and clean divider line',
                    },
                    {
                      id: 'vintage_crest',
                      name: '👑 Royal Vintage Crest',
                      desc: 'Laurel wreath and rose emblem, Latin-style desk tagline',
                    },
                    {
                      id: 'artisan_botanical',
                      name: '🌿 Botanical Studio',
                      desc: 'Pressed floral accents, artisan workshop aesthetic',
                    },
                  ].map((style) => (
                    <button
                      key={style.id}
                      onClick={() => updateLetter({ headerStyle: style.id as HeaderStyle })}
                      className={`p-3 rounded-xl border-2 text-left transition-all cursor-pointer ${
                        letter.headerStyle === style.id
                          ? 'bg-[#ffd75e] border-[#33314e] shadow-sm'
                          : 'bg-[#fffdf8] border-[#33314e]/40 hover:border-[#33314e]'
                      }`}
                    >
                      <span className="font-bold text-sm block text-[#33314e]">
                        {style.name}
                      </span>
                      <span className="text-xs text-[#514e6e] mt-0.5 block">{style.desc}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Sender & Organization Details */}
              <div className="jp-card p-5 space-y-3">
                <h3 className="font-extrabold text-base text-[#33314e]">
                  Sender & Organization Details
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">
                      Sender Name
                    </label>
                    <input
                      type="text"
                      value={letter.sender}
                      onChange={(e) => updateLetter({ sender: e.target.value })}
                      placeholder="Sabi"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e]"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">
                      Organization / Studio
                    </label>
                    <input
                      type="text"
                      value={letter.senderOrg}
                      onChange={(e) => updateLetter({ senderOrg: e.target.value })}
                      placeholder="Janapa Studio"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e]"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">
                      Title / Role
                    </label>
                    <input
                      type="text"
                      value={letter.senderTitle}
                      onChange={(e) => updateLetter({ senderTitle: e.target.value })}
                      placeholder="Creative Director"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e]"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">Date</label>
                    <input
                      type="text"
                      value={letter.date}
                      onChange={(e) => updateLetter({ date: e.target.value })}
                      placeholder="October 4, 2026"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e]"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">Sender Email</label>
                    <input
                      type="text"
                      value={letter.senderEmail}
                      onChange={(e) => updateLetter({ senderEmail: e.target.value })}
                      placeholder="loveforloveones@gmail.com"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e]"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-bold text-[#514e6e] block mb-1">Phone</label>
                    <input
                      type="text"
                      value={letter.senderPhone}
                      onChange={(e) => updateLetter({ senderPhone: e.target.value })}
                      placeholder="+1 (555) 392-8104"
                      className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-sm text-[#33314e]"
                    />
                  </div>
                </div>
              </div>

              {/* Stationery Paper Color */}
              <div className="jp-card p-5 space-y-3">
                <h3 className="font-extrabold text-base text-[#33314e]">
                  Stationery Paper Color
                </h3>
                <div className="grid grid-cols-3 gap-2.5">
                  {[
                    { id: 'cream_laid', name: 'Cream Laid', color: '#fffdf8' },
                    { id: 'vintage_parchment', name: 'Vintage', color: '#fdf6df' },
                    { id: 'blush_rose', name: 'Blush Rose', color: '#fceef3' },
                    { id: 'sage_mint', name: 'Sage Mint', color: '#eef5ee' },
                    { id: 'sky_pastel', name: 'Sky Pastel', color: '#edf4fa' },
                    { id: 'crisp_white', name: 'Crisp White', color: '#ffffff' },
                  ].map((p) => (
                    <button
                      key={p.id}
                      onClick={() => updateLetter({ paperTheme: p.id as PaperTheme })}
                      className={`h-16 rounded-xl border-2 flex flex-col items-center justify-center p-1.5 transition-transform cursor-pointer ${
                        letter.paperTheme === p.id
                          ? 'border-[#33314e] ring-2 ring-[#f06e9a] scale-105 shadow-sm'
                          : 'border-[#33314e]/40 hover:border-[#33314e]'
                      }`}
                      style={{ backgroundColor: p.color }}
                    >
                      <span className="text-xs font-bold text-[#33314e]">{p.name}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Stationery Paper Texture */}
              <div className="jp-card p-5 space-y-3">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-extrabold text-base text-[#33314e]">
                      Stationery Background Texture
                    </h3>
                    <p className="text-xs text-[#7e7c97]">
                      Tactile paper grain, natural deckled fibers, and cold-press relief
                    </p>
                  </div>
                  {/* Intensity Controls */}
                  <div className="flex items-center gap-1 bg-[#f8e2eb] p-1 rounded-xl border border-[#33314e]/40">
                    {(['subtle', 'medium', 'deep'] as const).map((lvl) => (
                      <button
                        key={lvl}
                        onClick={() => updateLetter({ textureIntensity: lvl })}
                        className={`px-2 py-0.5 rounded-lg text-[10px] font-bold capitalize transition-colors cursor-pointer ${
                          letter.textureIntensity === lvl
                            ? 'bg-[#33314e] text-[#fffdf8] shadow-xs'
                            : 'text-[#514e6e] hover:text-[#33314e]'
                        }`}
                      >
                        {lvl}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2.5 pt-1">
                  {[
                    { id: 'grain', name: 'Antique Grain', desc: 'Fine cold-press tooth & noise', icon: '📜' },
                    { id: 'fiber', name: 'Natural Fiber', desc: 'Handmade organic specks', icon: '🌾' },
                    { id: 'rough', name: 'Heavy Rough Rag', desc: 'Artisanal pressed paper', icon: '🪨' },
                    { id: 'linen', name: 'Linen Weave', desc: 'Delicate woven crosshatch', icon: '🧵' },
                    { id: 'laid', name: 'Ribbed Laid', desc: 'Traditional wire chainlines', icon: '📐' },
                    { id: 'smooth', name: 'Smooth Satin', desc: 'Clean untextured vellum', icon: '⚪' },
                  ].map((tex) => {
                    const isSelected = letter.paperTexture === tex.id;
                    return (
                      <button
                        key={tex.id}
                        onClick={() => updateLetter({ paperTexture: tex.id as PaperTexture })}
                        className={`p-3 rounded-xl border-2 text-left cursor-pointer transition-all ${
                          isSelected
                            ? 'bg-[#ffd75e] border-[#33314e] shadow-sm scale-[1.02]'
                            : 'bg-[#fffdf8] border-[#33314e]/40 hover:border-[#33314e]'
                        }`}
                      >
                        <div className="flex items-center gap-1.5">
                          <span className="text-base">{tex.icon}</span>
                          <span className="font-bold text-xs text-[#33314e] block">{tex.name}</span>
                        </div>
                        <span className="text-[10px] text-[#514e6e] mt-1 block leading-tight">
                          {tex.desc}
                        </span>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Typography & Ruled Lines */}
              <div className="jp-card p-5 space-y-4">
                <h3 className="font-extrabold text-base text-[#33314e]">
                  Typography & Guidelines
                </h3>

                <div className="grid grid-cols-2 gap-2.5">
                  {[
                    { id: 'handwritten', name: 'Handwritten Script', sub: 'Caveat Cursive' },
                    { id: 'serif_classic', name: 'Classic Serif', sub: 'Playfair Display' },
                    { id: 'mono_typewriter', name: 'Vintage Typewriter', sub: 'Courier Prime' },
                    { id: 'sans_clean', name: 'Modern Clean Sans', sub: 'Urbanist' },
                  ].map((font) => (
                    <button
                      key={font.id}
                      onClick={() => updateLetter({ fontChoice: font.id as FontChoice })}
                      className={`p-2.5 rounded-xl border-2 text-left cursor-pointer ${
                        letter.fontChoice === font.id
                          ? 'bg-[#ffd75e] border-[#33314e]'
                          : 'bg-[#fffdf8] border-[#33314e]/40 hover:border-[#33314e]'
                      }`}
                    >
                      <span className="font-bold text-xs block text-[#33314e]">{font.name}</span>
                      <span className="text-[10px] text-[#514e6e]">{font.sub}</span>
                    </button>
                  ))}
                </div>

                <div className="pt-2 flex items-center justify-between border-t border-[#33314e]/20">
                  <div>
                    <span className="font-bold text-xs text-[#33314e] block">
                      Ruled Writing Lines
                    </span>
                    <span className="text-[11px] text-[#7e7c97]">
                      Subtle lined stationery notebook guidelines
                    </span>
                  </div>
                  <input
                    type="checkbox"
                    checked={letter.showRuledLines}
                    onChange={(e) => updateLetter({ showRuledLines: e.target.checked })}
                    className="w-5 h-5 accent-[#33314e] rounded cursor-pointer"
                  />
                </div>
              </div>

              <button
                onClick={() => setActiveTab('envelope')}
                className="w-full jp-btn py-3 bg-[#f06e9a] text-white font-bold text-sm flex items-center justify-center gap-2 cursor-pointer hover:bg-[#e05b87]"
              >
                <span>Continue to Envelope & Seal →</span>
              </button>
            </div>
          )}

          {/* TAB 3: ENVELOPE & WAX SEAL */}
          {activeTab === 'envelope' && (
            <div className="jp-card p-6 space-y-4">
              <EnvelopeView
                letter={letter}
                onOpenLetter={() => setActiveTab('send')}
                onUpdateLetter={updateLetter}
              />
              <button
                onClick={() => setActiveTab('send')}
                className="w-full jp-btn py-3 bg-[#ffd75e] text-[#33314e] font-extrabold text-sm flex items-center justify-center gap-2 cursor-pointer hover:bg-[#ff9ec0]"
              >
                <span>Proceed to Send & Share Letter →</span>
              </button>
            </div>
          )}

          {/* TAB 6: SEND & SHARE (STANDALONE LINK + DIRECT EMAIL) */}
          {activeTab === 'send' && (
            <SendSharePanel
              letter={letter}
              onUpdateRecipientEmail={(email) => updateLetter({ recipientEmail: email })}
              onPreviewAsReceiver={() => setViewMode('recipient')}
            />
          )}
        </div>

        {/* RIGHT COLUMN: Persistent Live Paper Canvas (Desktop) */}
        <div className="w-full flex-1 flex flex-col items-center">
          <div className="w-full max-w-[620px] mb-3 flex items-center justify-between text-xs text-[#514e6e] font-semibold px-2">
            <span>Live Document Canvas</span>
            <span>Handcrafted stationery sheet</span>
          </div>

          <LetterCanvas
            canvasRef={canvasRef}
            letter={letter}
            interactive={true}
          />
        </div>
      </main>

      {/* FOOTER */}
      <footer className="border-t-2 border-[#33314e]/20 bg-[#f8e2eb]/40 py-6 px-4 text-center text-xs text-[#7e7c97] space-y-1.5 mt-auto">
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
          Letter Studio · Handcrafted digital stationery & romantic sealed letters
        </p>
      </footer>

      {/* MODAL 1: TEMPLATE SELECTION DIALOG */}
      {showTemplateModal && (
        <div className="fixed inset-0 z-50 bg-[#33314e]/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="jp-card w-full max-w-lg p-6 max-h-[90vh] overflow-y-auto space-y-4">
            <div className="flex items-center justify-between border-b border-[#33314e]/20 pb-3">
              <div>
                <h3 className="text-xl font-black text-[#33314e]">Select a Letter Template</h3>
                <p className="text-xs text-[#7e7c97]">
                  Choose from pre-crafted letterheads & heartfelt notes
                </p>
              </div>
              <button
                onClick={() => setShowTemplateModal(false)}
                className="w-8 h-8 rounded-full border border-[#33314e] flex items-center justify-center text-sm font-bold hover:bg-[#ffd75e] cursor-pointer"
              >
                ✕
              </button>
            </div>

            <div className="space-y-2.5">
              {[
                {
                  title: '💌 A Heartfelt Love Letter',
                  desc: 'Intimate cursive handwriting, crimson wax heart seal, and nostalgic stationery.',
                  data: {
                    recipient: 'Sophie',
                    recipientEmail: 'sophie@atelierdelumiere.com',
                    salutation: 'My dearest Sophie,',
                    subject: 'Some words deserve to be written down',
                    bodyText: `I wanted to send you something you could hold in your hands.

In a world of fleeting notifications, take a breath and know how deeply appreciated you are. Every idea we spoke about has begun to flourish, and every sunrise feels a little warmer remembering our shared laughter.

May this little letter find you surrounded by quiet peace, a warm cup in hand, and the gentle reminder that great things take time to unfold.`,
                    signOff: 'With all my love and fondness,',
                    sender: 'Sabi',
                    signatureText: 'Sabi',
                    headerStyle: 'romantic_personal' as HeaderStyle,
                    paperTheme: 'cream_laid' as PaperTheme,
                    paperTexture: 'grain' as PaperTexture,
                    textureIntensity: 'medium' as const,
                    fontChoice: 'handwritten' as FontChoice,
                    sealStyle: 'heart' as SealStyle,
                    sealColor: 'crimson' as SealColor,
                    showRuledLines: true,
                  },
                },
                {
                  title: '🏢 Official Executive Letterhead',
                  desc: 'Corporate memorandum with reference numbers, two-column contact grid, and formal typography.',
                  data: {
                    recipient: 'Board of Directors',
                    recipientEmail: 'board@vanguard-corp.eu',
                    salutation: 'Dear Members of the Board,',
                    subject: 'Quarterly Strategic Milestone & Technology Roadmap',
                    bodyText: `We are pleased to submit the strategic report for the upcoming fiscal cycle.

Over the past two quarters, our team has achieved seamless operational stability while deploying novel infrastructure enhancements across all primary domains. Client satisfaction indices have reached an unprecedented 98.4%, and efficiency metrics reflect significant improvements.

We look forward to presenting these findings during the annual summit and remain at your disposal for any further inquiries.`,
                    signOff: 'Respectfully submitted,',
                    sender: 'Dr. Alexander Wright',
                    senderTitle: 'Chief Strategy Officer',
                    senderOrg: 'VANGUARD INITIATIVE CORP.',
                    senderAddress: 'Suite 400, Financial Plaza, Frankfurt',
                    senderEmail: 'wright@vanguard-corp.eu',
                    senderPhone: '+49 (0) 69 9876 5432',
                    referenceNumber: 'REF: VG-2026/Q3-EXEC',
                    headerStyle: 'corporate_official' as HeaderStyle,
                    paperTheme: 'crisp_white' as PaperTheme,
                    paperTexture: 'smooth' as PaperTexture,
                    textureIntensity: 'subtle' as const,
                    fontChoice: 'serif_classic' as FontChoice,
                    sealStyle: 'rose_crest' as SealStyle,
                    sealColor: 'royal_navy' as SealColor,
                    showRuledLines: false,
                  },
                },
                {
                  title: '💐 Deep Gratitude & Appreciation',
                  desc: 'Warm gratitude letter for a mentor, teacher, or collaborative partner.',
                  data: {
                    recipient: 'Professor Elena Rostova',
                    recipientEmail: 'elena.rostova@finearts.edu',
                    salutation: 'Dear Professor Rostova,',
                    subject: 'Heartfelt Thanks for Your Guidance',
                    bodyText: `I am writing to express my deepest gratitude for your wisdom, patience, and encouraging mentorship throughout this transformative year.

Your faith in my abilities gave me the courage to pursue difficult questions and seek honest answers. I will forever carry the lessons learned in your studio into my future work.

Thank you for being such an extraordinary inspiration.`,
                    signOff: 'With sincere gratitude and warmth,',
                    sender: 'Clara Vance',
                    senderOrg: 'Institute of Fine Arts',
                    headerStyle: 'minimal_modern' as HeaderStyle,
                    paperTheme: 'vintage_parchment' as PaperTheme,
                    paperTexture: 'rough' as PaperTexture,
                    textureIntensity: 'medium' as const,
                    fontChoice: 'serif_classic' as FontChoice,
                    sealStyle: 'lily' as SealStyle,
                    sealColor: 'antique_gold' as SealColor,
                    showRuledLines: true,
                  },
                },
                {
                  title: '🎓 Letter of Recommendation',
                  desc: 'Academic or professional endorsement letterhead with monogram seal.',
                  data: {
                    recipient: 'Admissions Committee',
                    recipientEmail: 'admissions@fellowship-council.org',
                    salutation: 'To Whom It May Concern:',
                    subject: 'Letter of Recommendation for Julian Meyer',
                    bodyText: `It is my distinct privilege to recommend Julian Meyer for admission into your prestigious postgraduate fellowship program.

Having supervised Julian over the past three years, I have observed a rare combination of intellectual rigor, creative tenacity, and remarkable collegiality. He consistently approaches complex research problems with exceptional clarity and enthusiasm.

I offer Julian my highest and most unreserved recommendation.`,
                    signOff: 'Yours faithfully,',
                    sender: 'Prof. Marcus Sterling',
                    senderTitle: 'Chair of Applied Sciences',
                    senderOrg: 'University Research Council',
                    headerStyle: 'corporate_official' as HeaderStyle,
                    paperTheme: 'cream_laid' as PaperTheme,
                    paperTexture: 'laid' as PaperTexture,
                    textureIntensity: 'medium' as const,
                    fontChoice: 'serif_classic' as FontChoice,
                    sealStyle: 'monogram' as SealStyle,
                    sealColor: 'royal_navy' as SealColor,
                    initialMonogram: 'M',
                    showRuledLines: false,
                  },
                },
                {
                  title: '🌿 Artisan Studio Memo',
                  desc: 'Botanical aesthetic memo for craftspeople and design studios.',
                  data: {
                    recipient: 'Our Cherished Patrons',
                    recipientEmail: 'patrons@botanicalpress.com',
                    salutation: 'Dear Friends & Patrons,',
                    subject: 'Autumn Botanical Collection & Studio Notes',
                    bodyText: `As the first autumn leaves begin to turn, our studio doors open to reveal this season's handcrafted creations.

Each piece in this collection was shaped by hand using traditional slow-craft methods, celebrating organic textures and timeless beauty. We are overjoyed to share this new chapter with you.

Come visit us whenever you find yourself in the neighborhood.`,
                    signOff: 'Warmly from the workshop,',
                    sender: 'Mira & Studio Artisans',
                    senderOrg: 'The Botanical Press',
                    headerStyle: 'artisan_botanical' as HeaderStyle,
                    paperTheme: 'sage_mint' as PaperTheme,
                    paperTexture: 'fiber' as PaperTexture,
                    textureIntensity: 'deep' as const,
                    fontChoice: 'handwritten' as FontChoice,
                    sealStyle: 'bow' as SealStyle,
                    sealColor: 'sage_olive' as SealColor,
                    showRuledLines: true,
                  },
                },
              ].map((tpl, i) => (
                <button
                  key={i}
                  onClick={() => {
                    updateLetter(tpl.data);
                    setShowTemplateModal(false);
                  }}
                  className="w-full p-4 rounded-xl border-2 border-[#33314e] bg-[#fffdf8] hover:bg-[#ffd75e] transition-colors text-left cursor-pointer"
                >
                  <h4 className="font-extrabold text-sm text-[#33314e]">{tpl.title}</h4>
                  <p className="text-xs text-[#514e6e] mt-1">{tpl.desc}</p>
                </button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* MODAL 2: MOBILE FULL-PAGE PREVIEW */}
      {mobilePreviewModal && (
        <div className="fixed inset-0 z-50 bg-[#33314e]/70 backdrop-blur-sm flex flex-col p-4 overflow-y-auto">
          <div className="flex justify-between items-center mb-3">
            <span className="font-extrabold text-white text-sm">Full Document Preview</span>
            <button
              onClick={() => setMobilePreviewModal(false)}
              className="jp-btn px-4 py-1.5 bg-[#ffd75e] text-[#33314e] text-xs font-bold cursor-pointer"
            >
              Close
            </button>
          </div>
          <div className="my-auto">
            <LetterCanvas
              letter={letter}
              interactive={true}
            />
          </div>
        </div>
      )}
    </div>
  );
}
