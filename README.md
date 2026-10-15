# DCP

Course presentations and practical work written in Markdown and published with GitHub Pages.

## Local development

Install dependencies and open the live presentation server:

```sh
npm install
npm run dev
```

Build the website and PowerPoint files:

```sh
npm run build
```

The generated site is written to `dist/`.

## Adding material

Add a Marp Markdown file under either `slides/theory/` or `slides/practice/`. The build discovers it automatically and adds links for both the web presentation and its `.pptx` download to the home page.

Put images in `slides/assets/` and reference them as `![](../assets/file.png)`. The build copies that folder to `dist/assets/`, so the image works in both the web presentation and the `.pptx`. Diagrams written in Mermaid are pre-rendered to PNG and committed next to their `.mmd` source, e.g. `npx @mermaid-js/mermaid-cli -i slides/assets/diagram.mmd -o slides/assets/diagram.png -s 2 -b transparent`.

Files under `slides/drafts/` are not published. Move a draft to `slides/theory/` or `slides/practice/` when it is ready to be shown.

Use `---` to separate slides:

```md
---
marp: true
title: Topic title
---

# Topic title

---

## Next slide
```

Every push to `main` builds the material and publishes it to the `gh-pages` branch.
