import React from 'react';
import { InlineIcon } from '@iconify/react';
import githubOutlined from '@iconify/icons-ant-design/github-outlined';
import ThemedLink from './themed-link';
import { makeStyles, Theme, createStyles } from '@material-ui/core';
import Card from '@material-ui/core/Card';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      background: theme.palette.primary.dark,
      padding: theme.spacing(3, 4),
      display: 'flex',
      flexFlow: 'row nowrap'
    },
    linkContainer: {
      display: 'flex',
      flexGrow: 2,
      flexBasis: 1,
      '& > *': {
        margin: 'auto'
      }
    },
    leftGap: {
      flexGrow: 10
    }
  })
)

export default function Footer() {
  const classes = useStyles()
  return (
    <Card className={classes.root}>
      <div className={classes.leftGap} />
      <div className={classes.linkContainer}>
        <ThemedLink href="https://github.com/pjozsef/word-generator">
          Fork me on <InlineIcon icon={githubOutlined} height={32} />
        </ThemedLink>
      </div>
    </Card>
  )
}
