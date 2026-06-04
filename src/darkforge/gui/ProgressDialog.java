package darkforge.gui;

import darkforge.concurrency.BackgroundTask;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * Reusable modal progress dialog backed by a
 * BackgroundTask<?>. Construct with the task,
 * then call setVisible(true) on the EDT. The
 * dialog auto-closes when the task is DONE.
 *
 * Cancellation: the Cancel button calls
 * task.cancel(true). BackgroundTask.done()
 * silently swallows CancellationException,
 * so cancellation is a clean no-op for the
 * registered onResult / onError handlers.
 */
public class ProgressDialog
        extends JDialog {

    private static final long
            serialVersionUID = 1L;

    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public ProgressDialog(Frame owner,
                          String title,
                          BackgroundTask<?> task) {
        super(owner, title, true);

        progressBar = new JProgressBar(
                0, 100);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);

        statusLabel = new JLabel(
                "Working...");

        JButton cancel = new JButton(
                "Cancel");
        cancel.addActionListener(
                e -> task.cancel(true));

        JPanel content = new JPanel(
                new BorderLayout(8, 8));
        content.setBorder(BorderFactory
                .createEmptyBorder(
                        12, 12, 12, 12));
        content.add(statusLabel,
                BorderLayout.NORTH);
        content.add(progressBar,
                BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(cancel);
        content.add(buttons,
                BorderLayout.SOUTH);

        setContentPane(content);
        setSize(360, 140);
        setLocationRelativeTo(owner);

        task.addPropertyChangeListener(
                this::onTaskEvent);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void onTaskEvent(
            PropertyChangeEvent evt) {
        if ("progress".equals(
                evt.getPropertyName())) {
            int pct = (Integer)
                    evt.getNewValue();
            progressBar.setIndeterminate(
                    false);
            progressBar.setValue(pct);
        } else if ("state".equals(
                evt.getPropertyName())
                && SwingWorker.StateValue
                .DONE.equals(
                        evt.getNewValue())) {
            setVisible(false);
            dispose();
        }
    }
}