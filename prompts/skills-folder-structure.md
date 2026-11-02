Default project folder for each tool. Put each skill in its own directory with a `SKILL.md` inside.

| Tool | Project (repo) | Personal (all projects) |
|---|---|---|
| Cursor | `.cursor/skills/<skill-name>/SKILL.md` | `~/.cursor/skills/<skill-name>/SKILL.md` |
| VS Code | `.github/skills/<skill-name>/SKILL.md` | `~/.copilot/skills/<skill-name>/SKILL.md` |
| Claude Code | `.claude/skills/<skill-name>/SKILL.md` | `~/.claude/skills/<skill-name>/SKILL.md` |
| Antigravity | `.agents/skills/<skill-name>/SKILL.md` | `~/.gemini/config/skills/<skill-name>/SKILL.md` |
| GitHub Copilot | `.github/skills/<skill-name>/SKILL.md` | `~/.copilot/skills/<skill-name>/SKILL.md` |

Same shape everywhere:

```
<skills-root>/
└── my-skill/
    ├── SKILL.md          # required
    ├── scripts/          # optional
    └── references/       # optional
```

`SKILL.md` needs YAML frontmatter. `name` must match the folder name. `description` is what the agent uses to decide when to load it.

```markdown
---
name: my-skill
description: What it does and when to use it.
---
```

Extra paths those tools also load:

- **VS Code and Copilot** also read project skills from `.claude/skills/` and `.agents/skills/`, and personal skills from `~/.claude/skills/` and `~/.agents/skills/`. Copilot’s own default is still `.github/skills` and `~/.copilot/skills`.
- **Antigravity** still reads the old `.agent/skills` (no `s`). Current default is `.agents/skills`. Older global path `~/.gemini/antigravity/skills/` still works. CLI-only extras: `~/.gemini/antigravity-cli/skills/` and `~/.gemini/skills/` (the Antigravity app does not read those).
- **Claude.ai** (web/desktop) has no repo folder. Skills are uploaded to the account. The folders above are Claude Code.

One folder that VS Code, Copilot, and Antigravity all pick up: `.agents/skills/`. Cursor does not. Cursor only loads `.cursor/skills/` and `~/.cursor/skills/`.

Sources: [Cursor skills](https://cursor.com/docs/context/skills), [VS Code agent skills](https://code.visualstudio.com/docs/agent-customization/agent-skills), [Claude Code skills](https://code.claude.com/docs/en/skills), [Antigravity skills](https://antigravity.google/docs/skills), [Antigravity discovery](https://antigravity.google/docs/customization-discovery), [Copilot agent skills](https://docs.github.com/en/copilot/concepts/agents/about-agent-skills), [Copilot cheat sheet](https://docs.github.com/en/copilot/reference/customization-cheat-sheet).