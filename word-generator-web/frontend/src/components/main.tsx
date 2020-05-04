import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core';
import { WordsState } from '../redux/slices/words-slice';
import Words from './words';
import Categories from './categories';
import { CategoryActions, CategoriesState } from '../redux/slices/categories-slice';

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
  categoryActions: CategoryActions
  categoriesState: CategoriesState
}

export default function Main(props: Props) {
  const { words, categoryActions, categoriesState } = props
  const classes = useStyles()

  return (
    <div id="main" className={classes.root}>
      <Categories categoryActions={categoryActions} categoriesState={categoriesState}></Categories>
      <Words words={words}></Words>
    </div>
  )
}
