import React from 'react';
import './footer.css'
import { InlineIcon } from '@iconify/react';
import githubOutlined from '@iconify/icons-ant-design/github-outlined';
import ThemedLink from './themed-link';

export default function Footer() {
  return (
    <div id="footer">
      <ThemedLink href="https://github.com/pjozsef/word-generator">
        Fork me on <InlineIcon icon={githubOutlined} height={32} />
      </ThemedLink>
    </div>
  )
}
