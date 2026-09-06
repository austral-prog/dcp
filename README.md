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
