import React, { useState } from 'react';
import { LetterData } from '../types';
import { getLetterShareableUrl } from '../utils/letterLink';
import {
  Mail,
  Copy,
  Check,
  ExternalLink,
  Eye,
  Share2,
  MessageCircle,
  Link as LinkIcon,
  FileText,
  Sparkles,
  Send,
} from 'lucide-react';

interface SendSharePanelProps {
  letter: LetterData;
  onUpdateRecipientEmail: (email: string) => void;
  onPreviewAsReceiver: () => void;
}

export const SendSharePanel: React.FC<SendSharePanelProps> = ({
  letter,
  onUpdateRecipientEmail,
  onPreviewAsReceiver,
}) => {
  const [copiedType, setCopiedType] = useState<string | null>(null);

  const shareableUrl = getLetterShareableUrl(letter);
  const emailSubject =
    letter.subject || `A personal sealed letter for ${letter.recipient || 'you'}`;

  const recipientName = letter.recipient?.trim() || 'Friend';
  const senderName = letter.sender?.trim() || 'Letter Studio';

  const emailBodyText = `Dear ${recipientName},\n\nI have crafted a personal sealed letter for you.\n\n✨ Open and unseal your letter here:\n${shareableUrl}\n\nWarmly,\n${senderName}`;

  const fullEmailDraft = `To: ${letter.recipientEmail || ''}\nSubject: ${emailSubject}\n\n${emailBodyText}`;

  const formattedShareMessage = `💌 I have written a personalized letter for you: "${emailSubject}".\n\nOpen your sealed envelope here:\n${shareableUrl}`;

  const handleCopy = async (text: string, type: string) => {
    try {
      await navigator.clipboard.writeText(text);
      setCopiedType(type);
      setTimeout(() => setCopiedType(null), 2500);
    } catch (err) {
      console.error('Failed to copy', err);
    }
  };

  const handleOpenGmail = () => {
    const encodedTo = encodeURIComponent((letter.recipientEmail || '').trim());
    const encodedSub = encodeURIComponent(emailSubject);
    const encodedBody = encodeURIComponent(emailBodyText);

    window.open(
      `https://mail.google.com/mail/?view=cm&fs=1&to=${encodedTo}&su=${encodedSub}&body=${encodedBody}`,
      '_blank'
    );
  };

  const handleOpenMailto = () => {
    const encodedTo = encodeURIComponent((letter.recipientEmail || '').trim());
    const encodedSub = encodeURIComponent(emailSubject);
    const encodedBody = encodeURIComponent(emailBodyText);

    window.location.href = `mailto:${encodedTo}?subject=${encodedSub}&body=${encodedBody}`;
  };

  const handleShareWhatsApp = () => {
    window.open(
      `https://api.whatsapp.com/send?text=${encodeURIComponent(formattedShareMessage)}`,
      '_blank'
    );
  };

  const handleWebShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: emailSubject,
          text: `A personal sealed letter for you from ${letter.sender}`,
          url: shareableUrl,
        });
      } catch (err) {}
    } else {
      handleCopy(shareableUrl, 'link');
    }
  };

  return (
    <div className="space-y-4 font-urbanist">
      {/* 1. SEPARATE PERMANENT LINK CARD */}
      <div className="jp-card p-5 sm:p-6 space-y-3.5 bg-[#fffdf8]">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-[#ffd75e] border border-[#33314e] flex items-center justify-center">
              <LinkIcon size={16} className="text-[#33314e]" />
            </div>
            <div>
              <h3 className="font-extrabold text-base text-[#33314e]">
                Receiver's Independent Link
              </h3>
              <p className="text-xs text-[#7e7c97]">
                Anyone with this link opens the letter independently in its sealed envelope
              </p>
            </div>
          </div>

          <button
            onClick={onPreviewAsReceiver}
            className="jp-btn px-3 py-1.5 bg-[#fceef3] text-[#33314e] text-xs font-bold flex items-center gap-1.5 hover:bg-[#ff9ec0] cursor-pointer"
          >
            <Eye size={14} />
            <span className="hidden sm:inline">Preview as Receiver</span>
          </button>
        </div>

        {/* Link Input Box with Copy Button */}
        <div className="flex items-center gap-2">
          <input
            type="text"
            readOnly
            value={shareableUrl}
            className="flex-1 bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3 py-2 text-xs font-mono text-[#33314e] select-all truncate focus:outline-none"
          />
          <button
            onClick={() => handleCopy(shareableUrl, 'link')}
            className={`jp-btn px-4 py-2 font-bold text-xs flex items-center gap-1.5 shrink-0 cursor-pointer ${
              copiedType === 'link' ? 'bg-green-500 text-white' : 'bg-[#ffd75e] text-[#33314e]'
            }`}
          >
            {copiedType === 'link' ? <Check size={14} /> : <Copy size={14} />}
            <span>{copiedType === 'link' ? 'Copied!' : 'Copy Link'}</span>
          </button>
        </div>

        <div className="flex items-center justify-between text-[11px] text-[#7e7c97] pt-1">
          <span>🔒 Stored securely in standalone link format</span>
          <button
            onClick={() => window.open(shareableUrl, '_blank')}
            className="text-[#f06e9a] font-bold hover:underline flex items-center gap-1 cursor-pointer"
          >
            <span>Open in New Tab</span>
            <ExternalLink size={12} />
          </button>
        </div>
      </div>

      {/* 2. DRAFT EMAIL CARD (EASY TO COPY) */}
      <div className="jp-card p-5 sm:p-6 space-y-4 bg-[#fffdf8] border-2 border-[#33314e]">
        <div className="flex items-center justify-between flex-wrap gap-2">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-full bg-[#f06e9a] text-white border border-[#33314e] flex items-center justify-center">
              <Mail size={16} />
            </div>
            <div>
              <h3 className="font-extrabold text-base text-[#33314e]">
                Draft Email for Receiver
              </h3>
              <p className="text-xs text-[#514e6e]">
                Copy this pre-written invitation to paste directly into your email
              </p>
            </div>
          </div>

          {/* Top Quick Copy Button */}
          <button
            onClick={() => handleCopy(fullEmailDraft, 'all')}
            className={`jp-btn px-4 py-2 text-xs font-black flex items-center gap-1.5 cursor-pointer ${
              copiedType === 'all'
                ? 'bg-green-500 text-white'
                : 'bg-[#ffd75e] text-[#33314e] hover:bg-[#ff9ec0]'
            }`}
          >
            {copiedType === 'all' ? <Check size={14} /> : <Copy size={14} />}
            <span>{copiedType === 'all' ? 'Copied Entire Draft!' : 'Copy Entire Email Draft'}</span>
          </button>
        </div>

        {/* Email Fields & Content Box */}
        <div className="space-y-3 bg-[#fceef3]/30 p-4 rounded-xl border-2 border-[#33314e]">
          {/* Receiver Email (Optional) */}
          <div>
            <label className="text-xs font-bold text-[#514e6e] block mb-1">
              To (Receiver's Email)
            </label>
            <input
              type="email"
              value={letter.recipientEmail}
              onChange={(e) => onUpdateRecipientEmail(e.target.value)}
              placeholder="e.g. friend@example.com (optional)"
              className="w-full bg-[#fffdf8] border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-xs sm:text-sm text-[#33314e] focus:outline-none"
            />
          </div>

          {/* Subject Line with Quick Copy */}
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-bold text-[#514e6e]">Subject</label>
              <button
                onClick={() => handleCopy(emailSubject, 'subject')}
                className="text-[11px] font-bold text-[#f06e9a] hover:underline flex items-center gap-1 cursor-pointer"
              >
                {copiedType === 'subject' ? <Check size={12} className="text-green-600" /> : <Copy size={12} />}
                <span>{copiedType === 'subject' ? 'Copied!' : 'Copy Subject'}</span>
              </button>
            </div>
            <div className="w-full bg-[#fffdf8] border-2 border-[#33314e] rounded-xl px-3.5 py-2 text-xs sm:text-sm text-[#33314e] font-semibold truncate select-all">
              {emailSubject}
            </div>
          </div>

          {/* Email Body with Quick Copy */}
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-bold text-[#514e6e]">Email Message Body</label>
              <button
                onClick={() => handleCopy(emailBodyText, 'body')}
                className="text-[11px] font-bold text-[#f06e9a] hover:underline flex items-center gap-1 cursor-pointer"
              >
                {copiedType === 'body' ? <Check size={12} className="text-green-600" /> : <Copy size={12} />}
                <span>{copiedType === 'body' ? 'Copied!' : 'Copy Body Text'}</span>
              </button>
            </div>
            <div className="w-full bg-[#fffdf8] border-2 border-[#33314e] rounded-xl p-3.5 text-xs text-[#33314e] font-mono whitespace-pre-line leading-relaxed select-all">
              {emailBodyText}
            </div>
          </div>

          {/* Primary Action: Copy Full Draft */}
          <button
            onClick={() => handleCopy(fullEmailDraft, 'all')}
            className={`w-full jp-btn py-3 text-sm font-black flex items-center justify-center gap-2 cursor-pointer shadow-sm ${
              copiedType === 'all'
                ? 'bg-green-500 text-white'
                : 'bg-[#ffd75e] text-[#33314e] hover:bg-[#ff9ec0]'
            }`}
          >
            {copiedType === 'all' ? (
              <>
                <Check size={17} />
                <span>Copied Draft to Clipboard! Ready to Paste</span>
              </>
            ) : (
              <>
                <Copy size={17} />
                <span>Copy Draft Email (Subject & Message)</span>
              </>
            )}
          </button>

          {/* Quick Launcher Options */}
          <div className="pt-2 border-t border-[#33314e]/20">
            <p className="text-[11px] font-bold text-[#7e7c97] uppercase tracking-wider mb-2 text-center">
              Or open directly in your email app
            </p>
            <div className="grid grid-cols-2 gap-2">
              <button
                onClick={handleOpenGmail}
                className="jp-btn py-2 px-3 bg-[#fffdf8] text-[#33314e] text-xs font-bold flex items-center justify-center gap-1.5 cursor-pointer hover:bg-[#ffd75e]"
              >
                <Mail size={14} className="text-[#c1392b]" />
                <span>Open in Gmail</span>
              </button>

              <button
                onClick={handleOpenMailto}
                className="jp-btn py-2 px-3 bg-[#fffdf8] text-[#33314e] text-xs font-bold flex items-center justify-center gap-1.5 cursor-pointer hover:bg-[#ffd75e]"
              >
                <ExternalLink size={14} />
                <span>Open in Mail App</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* 3. INSTANT CHAT & SOCIAL SHARE */}
      <div className="jp-card p-5 space-y-3 bg-[#fffdf8]">
        <h4 className="font-extrabold text-sm text-[#33314e]">Share via Message or Social</h4>
        <div className="grid grid-cols-2 gap-2">
          <button
            onClick={handleShareWhatsApp}
            className="jp-btn py-2.5 px-3 bg-[#fffdf8] text-[#33314e] text-xs font-bold flex items-center justify-center gap-1.5 cursor-pointer hover:bg-[#ffd75e]"
          >
            <MessageCircle size={15} className="text-green-600" />
            <span>WhatsApp</span>
          </button>

          <button
            onClick={handleWebShare}
            className="jp-btn py-2.5 px-3 bg-[#fffdf8] text-[#33314e] text-xs font-bold flex items-center justify-center gap-1.5 cursor-pointer hover:bg-[#ffd75e]"
          >
            <Share2 size={15} className="text-[#f06e9a]" />
            <span>More Apps...</span>
          </button>
        </div>
      </div>
    </div>
  );
};
