import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

export async function exportToPdf(
  element: HTMLElement,
  filename: string = 'letterhead-document',
  format: 'a4' | 'letter' = 'a4'
): Promise<boolean> {
  try {
    const canvas = await html2canvas(element, {
      scale: 2.5,
      useCORS: true,
      logging: false,
      backgroundColor: null,
    });

    const imgData = canvas.toDataURL('image/png');

    // Page dimensions in mm
    const isA4 = format === 'a4';
    const pageWidth = isA4 ? 210 : 215.9;
    const pageHeight = isA4 ? 297 : 279.4;

    const pdf = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: isA4 ? 'a4' : 'letter',
    });

    // Fit image cleanly on page with standard 10mm margins
    const margin = 10;
    const renderWidth = pageWidth - margin * 2;
    const renderHeight = (canvas.height * renderWidth) / canvas.width;

    const topOffset = renderHeight < pageHeight - margin * 2
      ? (pageHeight - renderHeight) / 2
      : margin;

    pdf.addImage(imgData, 'PNG', margin, topOffset, renderWidth, renderHeight, '', 'FAST');
    pdf.save(`${filename}.pdf`);
    return true;
  } catch (error) {
    console.error('Error generating PDF:', error);
    return false;
  }
}

export async function exportToPng(
  element: HTMLElement,
  filename: string = 'letterhead-document'
): Promise<boolean> {
  try {
    const canvas = await html2canvas(element, {
      scale: 3,
      useCORS: true,
      logging: false,
      backgroundColor: null,
    });

    const link = document.createElement('a');
    link.download = `${filename}.png`;
    link.href = canvas.toDataURL('image/png');
    link.click();
    return true;
  } catch (error) {
    console.error('Error exporting PNG:', error);
    return false;
  }
}

export function printLetter(): void {
  window.print();
}
