import express, { Request, Response } from 'express';
import nodemailer from 'nodemailer';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = process.env.PORT ? parseInt(process.env.PORT, 10) : 3000;

app.use(express.json({ limit: '10mb' }));

// Direct Email Dispatch Route
app.post('/api/send-letter', async (req: Request, res: Response): Promise<void> => {
  try {
    const {
      to,
      recipientName,
      senderName,
      senderEmail,
      subject,
      bodyPreview,
      letterUrl,
    } = req.body;

    if (!to) {
      res.status(400).json({ error: 'Recipient email is required' });
      return;
    }

    const emailSubject = subject || `A personal letter for ${recipientName || 'you'} from ${senderName || 'someone special'}`;
    const cleanLetterUrl = letterUrl || 'https://letter.janapasabi.com';

    // HTML Email template in the Janapasabi aesthetic
    const htmlContent = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${emailSubject}</title>
</head>
<body style="margin: 0; padding: 0; background-color: #f8e2eb; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #33314e;">
  <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #f8e2eb; padding: 40px 15px;">
    <tr>
      <td align="center">
        <!-- Main Card -->
        <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 540px; background-color: #fffdf8; border: 2px solid #33314e; border-radius: 20px; box-shadow: 4px 4px 0px #33314e; overflow: hidden;">
          <!-- Header Banner -->
          <tr>
            <td style="padding: 28px 30px; text-align: center; border-bottom: 2px dashed #eed6e1; background-color: #fcf4f8;">
              <div style="display: inline-block; width: 44px; height: 44px; line-height: 44px; font-size: 26px; background-color: #ffd75e; border: 2px solid #33314e; border-radius: 50%; box-shadow: 2px 2px 0px #33314e; margin-bottom: 12px;">
                💌
              </div>
              <h1 style="margin: 0; font-size: 22px; font-weight: 800; color: #33314e; letter-spacing: -0.5px;">
                You Have Received a Sealed Letter
              </h1>
              <p style="margin: 6px 0 0 0; font-size: 13px; color: #6e6b89;">
                A personalized letter was handwritten for you by <strong style="color: #33314e;">${senderName || 'Someone'}</strong>
              </p>
            </td>
          </tr>

          <!-- Letter Excerpt -->
          <tr>
            <td style="padding: 30px 32px;">
              <p style="margin: 0 0 16px 0; font-size: 16px; font-weight: 700; color: #33314e;">
                Dear ${recipientName || 'Friend'},
              </p>

              <div style="background-color: #fffaf0; border-left: 4px solid #f06e9a; border-radius: 8px; padding: 16px 20px; margin: 18px 0; font-size: 14px; line-height: 1.6; color: #474465; font-style: italic;">
                "${(bodyPreview || '').slice(0, 240)}${(bodyPreview || '').length > 240 ? '...' : ''}"
              </div>

              <p style="margin: 18px 0; font-size: 14px; line-height: 1.5; color: #5b587a;">
                Your letter has been prepared with custom stationery, wax sealing, and decorative stamps. Tap the button below to break the wax seal and open the letter:
              </p>

              <!-- CTA Button -->
              <div style="text-align: center; margin: 30px 0 20px 0;">
                <a href="${cleanLetterUrl}" target="_blank" style="display: inline-block; background-color: #ffd75e; color: #33314e; font-size: 15px; font-weight: 800; text-decoration: none; padding: 14px 34px; border: 2px solid #33314e; border-radius: 14px; box-shadow: 3px 3px 0px #33314e;">
                  ✨ Open Your Sealed Letter ✨
                </a>
              </div>

              <p style="text-align: center; margin: 0; font-size: 11px; color: #8f8ba3;">
                Or open this link directly in your browser:<br/>
                <a href="${cleanLetterUrl}" style="color: #f06e9a; word-break: break-all; text-decoration: underline;">${cleanLetterUrl}</a>
              </p>
            </td>
          </tr>

          <!-- Footer -->
          <tr>
            <td style="padding: 18px 30px; text-align: center; background-color: #fdfaf8; border-top: 2px solid #eed6e1; font-size: 11px; color: #8e8b9e;">
              Sent via Letter Studio · Handcrafted digital stationery & sealed letters
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
    `;

    const defaultSenderEmail = 'loveforloveones@gmail.com';
    const effectiveSenderEmail = senderEmail || defaultSenderEmail;

    // Check if live SMTP credentials are configured (or supplied via request)
    const userSmtpPass = req.body.smtpPassword || process.env.SMTP_PASS;
    const smtpHost = process.env.SMTP_HOST || 'smtp.gmail.com';
    const smtpPort = process.env.SMTP_PORT ? parseInt(process.env.SMTP_PORT, 10) : 465;
    const smtpUser = process.env.SMTP_USER || effectiveSenderEmail;

    if (userSmtpPass) {
      try {
        const transporter = nodemailer.createTransport({
          host: smtpHost,
          port: smtpPort,
          secure: smtpPort === 465,
          auth: {
            user: smtpUser,
            pass: userSmtpPass.replace(/\s+/g, ''),
          },
        });

        await transporter.sendMail({
          from: `"${senderName || 'Letter Studio'}" <${smtpUser}>`,
          to,
          replyTo: effectiveSenderEmail,
          subject: emailSubject,
          html: htmlContent,
        });

        res.json({
          success: true,
          sentDirectly: true,
          message: `Letter dispatched directly from ${smtpUser} to ${to}!`,
          letterUrl: cleanLetterUrl,
        });
        return;
      } catch (smtpErr: any) {
        console.error('SMTP delivery failed:', smtpErr);
        res.json({
          success: false,
          sentDirectly: false,
          error: `SMTP error: ${smtpErr.message || 'Authentication failed'}. Opening Gmail to send directly.`,
          requiresClientDispatch: true,
        });
        return;
      }
    }

    // When running without SMTP password configured:
    console.log(`[EMAIL DISPATCH] Requires client dispatch for ${to}:`);
    console.log(`From: ${senderName || 'Letter Studio'} <${effectiveSenderEmail}>`);
    console.log(`To: ${to}`);
    console.log(`Subject: ${emailSubject}`);
    console.log(`Letter URL: ${cleanLetterUrl}`);

    res.json({
      success: true,
      sentDirectly: false,
      requiresClientDispatch: true,
      message: 'Opening your email composer to deliver the sealed letter directly to the recipient.',
      letterUrl: cleanLetterUrl,
    });
  } catch (error: any) {
    console.error('Error dispatching letter email:', error);
    res.status(500).json({
      error: 'Failed to send email',
      details: error.message,
    });
  }
});

// Endpoint to verify connection to sender email account via SMTP
app.post('/api/test-smtp', async (req, res) => {
  try {
    const { smtpPassword, senderEmail } = req.body;
    const effectiveSender = senderEmail || 'loveforloveones@gmail.com';
    const password = smtpPassword || process.env.SMTP_PASS;

    if (!password) {
      return res.status(400).json({
        success: false,
        error: 'Please enter the 16-character App Password for ' + effectiveSender,
      });
    }

    const transporter = nodemailer.createTransport({
      host: 'smtp.gmail.com',
      port: 465,
      secure: true,
      auth: {
        user: effectiveSender,
        pass: password.replace(/\s+/g, ''),
      },
    });

    await transporter.verify();
    res.json({
      success: true,
      message: `Successfully connected to ${effectiveSender}! You can now send letters directly in the background.`,
    });
  } catch (err: any) {
    console.error('SMTP verification failed:', err);
    res.status(400).json({
      success: false,
      error: err.message || 'Could not authenticate with Google SMTP. Check password.',
    });
  }
});

// Full-Stack Server Integration with Vite
async function startServer() {
  // Static audio route for Sahiba song
  app.get(['/Sahiba.mp3', '/sahiba.mp3', '/sahiba.mp4', '/Sahiba.mp4'], (_req: Request, res: Response) => {
    const audioPath = path.resolve(__dirname, 'public', 'Sahiba.mp3');
    res.setHeader('Content-Type', 'audio/mpeg');
    res.sendFile(audioPath);
  });

  if (process.env.NODE_ENV === 'production') {
    app.use(express.static(path.resolve(__dirname, 'dist')));
    app.use((_req: Request, res: Response) => {
      res.sendFile(path.resolve(__dirname, 'dist', 'index.html'));
    });
  } else {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Letter Studio server listening on http://0.0.0.0:${PORT}`);
  });
}

startServer();
