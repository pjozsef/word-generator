import React from 'react';
import { makeStyles, Theme, createStyles, useTheme } from '@material-ui/core';
import { WordsState } from '../redux/slices/words-slice';
import Words from './words';
import Categories from './categories';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      background: theme.palette.background.default,
      display: 'flex',
      flexFlow: 'row nowrap',
      overflow: 'hidden',
      '& .MuiCard-root': {
        color: theme.palette.primary.dark
      },
      '& > *':{
        margin: theme.spacing(5, 15)
      }
    }
  })
)

type Props = {
  words: WordsState

}

export default function Main(props: Props) {
  const { words } = props
  const classes = useStyles()
  const theme = useTheme()

  return (
    <div id="main" className={classes.root}>
      <Categories></Categories>
      <Words words={words}></Words>
    </div>
  )
}
