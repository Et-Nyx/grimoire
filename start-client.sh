#!/usr/bin/env bash
# Wrapper script to run Grimoire TUI Client

# Only create symlink if it doesn't exist yet
if [ ! -e "/bin/stty" ]; then
    STTY_PATH=$(which stty 2>/dev/null)
    
    if [ -n "$STTY_PATH" ]; then
        echo "First run: Creating /bin/stty symlink for Lanterna compatibility..."
        echo "stty found at: $STTY_PATH"
        
        # Try creating system symlink (requires sudo only once)
        if sudo mkdir -p /bin 2>/dev/null && sudo ln -sf "$STTY_PATH" /bin/stty 2>/dev/null; then
            echo "✓ Successfully created /bin/stty symlink (one-time setup)"
            echo "  Future runs won't need sudo."
        else
            echo "✗ Could not create /bin/stty symlink"
            echo "  You may need to run: sudo ln -sf $STTY_PATH /bin/stty"
        fi
    else
        echo "Warning: stty not found in PATH"
    fi
else
    echo "Using existing /bin/stty"
fi

# Run the application
exec java -jar "$(dirname "$0")/tui/target/grimoire-tui-0.0.1-SNAPSHOT-jar-with-dependencies.jar" "$@"
