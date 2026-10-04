import React, { useState } from 'react';
import { LetterData } from '../types';
import { Mail, Send, Copy, Check, ExternalLink, Sparkles, X } from 'lucide-react';

interface EmailDispatchModalProps {
  letter: LetterData;
  onUpdateRecipientEmail: (email: string) => void;
  onClose: () => void;
}

export const EmailDispatchModal: React.FC<EmailDispatchModalProps> = ({
  letter,
  onUpdateRecipientEmail,
  onClose,
}) => {
  const [copied, setCopied] = useState(false);
  const [emailStatus, setEmailStatus] = useState<string | null>(null);

  const formattedEmailBody = `${letter.salutation || `Dear ${letter.recipient},`}\n\n${letter.bodyText}\n\n${letter.signOff || 'With all my love,'}\n${letter.signatureText || letter.sender}\n${[letter.senderTitle, letter.senderOrg].filter(Boolean).join(' · ')}`;

  const emailSubject = letter.subject || `A personalized letter for ${letter.recipient}`;

  const handleSendViaMailto = () => {
    const encodedTo = encodeURIComponent(letter.recipientEmail.trim());
    const encodedSubject = encodeURIComponent(emailSubject);
    const encodedBody = encodeURIComponent(formattedEmailBody);

    const mailtoUrl = `mailto:${encodedTo}?subject=${encodedSubject}&body=${encodedBody}`;
    window.location.href = mailtoUrl;
    setEmailStatus('Opening your default email client...');
  };

  const handleOpenGmailWeb = () => {
    const encodedTo = encodeURIComponent(letter.recipientEmail.trim());
    const encodedSubject = encodeURIComponent(emailSubject);
    const encodedBody = encodeURIComponent(formattedEmailBody);

    const gmailUrl = `https://mail.google.com/mail/?view=cm&fs=1&to=${encodedTo}&su=${encodedSubject}&body=${encodedBody}`;
    window.open(gmailUrl, '_blank', 'noopener,noreferrer');
    setEmailStatus('Opened Gmail composer in a new tab.');
  };

  const handleCopyText = async () => {
    try {
      await navigator.clipboard.writeText(
        `Subject: ${emailSubject}\nTo: ${letter.recipientEmail}\n\n${formattedEmailBody}`
      );
      setCopied(true);
      setTimeout(() => setCopied(false), 2500);
    } catch (e) {
      console.error(e);
    }
  };

  const handleWebShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: emailSubject,
          text: formattedEmailBody,
        });
        setEmailStatus('Shared successfully!');
      } catch (err) {
        // user cancelled or share failed
      }
    } else {
      handleSendViaMailto();
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-[#33314e]/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="jp-card w-full max-w-lg p-6 max-h-[92vh] overflow-y-auto space-y-4">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-[#33314e]/20 pb-3">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-[#ffd75e] border border-[#33314e] flex items-center justify-center">
              <Mail size={16} className="text-[#33314e]" />
            </div>
            <div>
              <h3 className="text-xl font-black text-[#33314e]">Send Letter Directly</h3>
              <p className="text-xs text-[#7e7c97]">
                Dispatch your correspondence straight to the recipient's inbox
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full border border-[#33314e] flex items-center justify-center text-sm font-bold hover:bg-[#ffd75e] cursor-pointer"
          >
            <X size={16} />
          </button>
        </div>

        {/* Recipient Email Input */}
        <div className="space-y-3">
          <div>
            <label className="text-xs font-bold text-[#514e6e] block mb-1">
              Recipient Email Address
            </label>
            <input
              type="email"
              value={letter.recipientEmail}
              onChange={(e) => onUpdateRecipientEmail(e.target.value)}
              placeholder="e.g. sophie@atelierdelumiere.com"
              className="w-full bg-[#f8e2eb]/40 border-2 border-[#33314e] rounded-xl px-3.5 py-2.5 text-sm text-[#33314e] focus:outline-none focus:ring-2 focus:ring-[#f06e9a]"
            />
          </div>

          <div>
            <label className="text-xs font-bold text-[#514e6e] block mb-1">
              Email Subject Line
            </label>
            <div className="bg-[#fffdf8] border-2 border-[#33314e]/30 rounded-xl px-3.5 py-2 text-xs font-semibold text-[#33314e]">
              {emailSubject}
            </div>
          </div>
        </div>

        {/* Email Preview Box */}
        <div className="bg-[#f8e2eb]/30 border-2 border-dashed border-[#33314e]/40 rounded-xl p-3.5 space-y-1.5 text-xs text-[#514e6e] font-urbanist max-h-44 overflow-y-auto">
          <span className="font-bold uppercase tracking-wider text-[10px] text-[#f06e9a] block">
            Email Content Preview
          </span>
          <p className="font-semibold text-[#33314e]">{letter.salutation || `Dear ${letter.recipient},`}</p>
          <p className="whitespace-pre-line leading-relaxed">{letter.bodyText}</p>
          <div className="pt-2 border-t border-[#33314e]/10 text-right">
            <p className="italic">{letter.signOff}</p>
            <p className="font-bold text-[#33314e] font-handwriting text-base">
              {letter.signatureText || letter.sender}
            </p>
          </div>
        </div>

        {/* Direct Dispatch Options */}
        <div className="space-y-2 pt-1">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
            {/* Open Gmail Web */}
            <button
              onClick={handleOpenGmailWeb}
              className="jp-btn py-3 px-4 bg-[#ffd75e] text-[#33314e] font-bold text-xs sm:text-sm flex items-center justify-center gap-2 cursor-pointer"
            >
              <Send size={16} />
              <span>Send via Gmail Web</span>
            </button>

            {/* Default Mail Client (mailto) */}
            <button
              onClick={handleSendViaMailto}
              className="jp-btn py-3 px-4 bg-[#fffdf8] text-[#33314e] font-bold text-xs sm:text-sm flex items-center justify-center gap-2 cursor-pointer"
            >
              <ExternalLink size={16} />
              <span>Open in Mail Client</span>
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 pt-1">
            {/* Copy Email Text */}
            <button
              onClick={handleCopyText}
              className="jp-btn py-2.5 px-3 bg-[#fffdf8] text-[#33314e] font-bold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
            >
              {copied ? <Check size={14} className="text-green-600" /> : <Copy size={14} />}
              <span>{copied ? 'Copied to Clipboard!' : 'Copy Formatted Text'}</span>
            </button>

            {/* Native Web Share */}
            {typeof navigator !== 'undefined' && 'share' in navigator && (
              <button
                onClick={handleWebShare}
                className="jp-btn py-2.5 px-3 bg-[#fceef3] text-[#33314e] font-bold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <Sparkles size={14} className="text-[#f06e9a]" />
                <span>Share via Mobile Apps</span>
              </button>
            )}
          </div>
        </div>

        {emailStatus && (
          <p className="text-center text-xs font-semibold text-green-700 bg-green-50 border border-green-200 rounded-lg py-2">
            ✓ {emailStatus}
          </p>
        )}
      </div>
    </div>
  );
};
