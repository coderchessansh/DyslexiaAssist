using System.Diagnostics;
using System.Drawing.Drawing2D;
using System.Speech.Synthesis;
using System.Windows.Forms;

namespace DyslexiaAssist.Windows;

internal static class Program
{
    [STAThread]
    static void Main()
    {
        ApplicationConfiguration.Initialize();
        Application.Run(new MainForm());
    }
}

public sealed class MainForm : Form
{
    readonly TextBox reader = new();
    readonly Label preview = new();
    readonly SpeechSynthesizer speech = new();
    readonly Color purple = Color.FromArgb(108, 76, 255);
    float size = 24;

    public MainForm()
    {
        Text = "DyslexiaAssist";
        StartPosition = FormStartPosition.CenterScreen;
        MinimumSize = new Size(820, 620);
        Size = new Size(980, 700);
        BackColor = Color.FromArgb(247, 245, 252);
        Font = new Font("Segoe UI", 10);

        var header = new Panel { Dock=DockStyle.Top, Height=105, BackColor=Color.FromArgb(42,31,76), Padding=new Padding(28,18,28,15) };
        Controls.Add(header);

        var logo = new Panel { Size=new Size(64,64), Location=new Point(28,20), BackColor=purple };
        logo.Paint += (_,e) => { using var b=new SolidBrush(Color.White); e.Graphics.SmoothingMode=SmoothingMode.AntiAlias; e.Graphics.FillEllipse(b,18,13,28,28); using var m=new SolidBrush(Color.FromArgb(130,255,218)); e.Graphics.FillEllipse(m,24,39,16,16); };
        header.Controls.Add(logo);

        var title = new Label { Text="DyslexiaAssist", ForeColor=Color.White, Font=new Font("Segoe UI Semibold",25), AutoSize=true, Location=new Point(108,17) };
        header.Controls.Add(title);
        header.Controls.Add(new Label { Text="A calm space for easier reading & word practice", ForeColor=Color.FromArgb(210,205,230), AutoSize=true, Location=new Point(110,58) });

        var tabs = new TabControl { Dock=DockStyle.Fill, Padding=new Point(18,8) };
        Controls.Add(tabs);
        var easy = new TabPage("  Easy Reader  ") { BackColor=BackColor };
        var words = new TabPage("  Word Practice  ") { BackColor=BackColor };
        tabs.TabPages.Add(easy); tabs.TabPages.Add(words);

        reader.Multiline=true; reader.ScrollBars=ScrollBars.Vertical; reader.Font=new Font("Segoe UI",16); reader.Dock=DockStyle.Top; reader.Height=170;
        reader.Text="Type or paste text here…";
        easy.Controls.Add(reader);

        var controls = new FlowLayoutPanel { Dock=DockStyle.Top, Height=65, Padding=new Padding(0,12,0,8), FlowDirection=FlowDirection.LeftToRight };
        easy.Controls.Add(controls);
        AddButton(controls,"▶ Read aloud",()=>Speak(reader.Text));
        AddButton(controls,"A−",()=>ChangeSize(-2));
        AddButton(controls,"A+",()=>ChangeSize(2));
        AddButton(controls,"Clear",()=>reader.Clear());

        preview.Text="Preview";
        preview.Font=new Font("Segoe UI",size);
        preview.AutoSize=false; preview.Dock=DockStyle.Fill; preview.Padding=new Padding(18); preview.BackColor=Color.White;
        easy.Controls.Add(preview);
        reader.TextChanged += (_,_)=>preview.Text=reader.Text;

        var intro=new Label { Text="Tap a word to hear it spoken.", AutoSize=true, Font=new Font("Segoe UI Semibold",14), Padding=new Padding(18) };
        words.Controls.Add(intro);
        var flow=new FlowLayoutPanel { Dock=DockStyle.Top, Top=55, Height=260, Padding=new Padding(18), AutoScroll=true };
        words.Controls.Add(flow);
        foreach(var w in new[]{"apple","window","school","friend","planet","because","beautiful","together","remember","different"})
            AddButton(flow,w,()=>Speak(w),150,52);

        FormClosed += (_,_)=>speech.Dispose();
    }

    void Speak(string text)
    {
        if(string.IsNullOrWhiteSpace(text)) return;
        speech.SpeakAsyncCancelAll();
        speech.SpeakAsync(text);
    }
    void ChangeSize(float delta)
    {
        size=Math.Clamp(size+delta,16,42);
        preview.Font=new Font("Segoe UI",size);
    }
    void AddButton(Control parent,string text,Action action,int width=150,int height=42)
    {
        var b=new Button { Text=text, Width=width, Height=height, FlatStyle=FlatStyle.Flat, BackColor=purple, ForeColor=Color.White, Margin=new Padding(6), Cursor=Cursors.Hand };
        b.FlatAppearance.BorderSize=0; b.Click+=(_,_)=>action(); parent.Controls.Add(b);
    }
}