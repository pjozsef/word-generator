import React from 'react';
import './footer.css'
import { InlineIcon } from '@iconify/react';
import githubOutlined from '@iconify/icons-ant-design/github-outlined';

export default function Footer() {
    return (
      <div id="footer">
        <a href="https://github.com/pjozsef/word-generator">
          Fork me on <InlineIcon icon={githubOutlined} height={32}/>
        </a>
      </div>
    )
  }
