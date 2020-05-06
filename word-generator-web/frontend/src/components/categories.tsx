import React, { ChangeEvent } from 'react';
import { makeStyles, Theme, createStyles, TextField } from '@material-ui/core';
import Card from '@material-ui/core/Card';
import { CategoryActions, CategoriesState } from '../redux/slices/categories-slice';
import CategoryTabs from './category-tabs';
import CategoryAdd from './category-add';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      flexGrow: 1,
      flexShrink: 0,
      flexFlow: 'row nowrap',
      alignItems: 'stretch',
      background: theme.palette.primary.light
    },
    tabCard: {
      display: 'flex',
      width: '35%',
      overflow: 'hidden',
      flexFlow: 'column nowrap'
    },
    textArea: {
      width: '100%',
      flexGrow: 1,
      margin: theme.spacing(2),
      selfAlign: 'center'
    },
    dialogActions: {
      display: 'flex',
      justifyContent: 'space-around'
    }
  })
)
type Props = {
  categoryActions: CategoryActions,
  categoriesState: CategoriesState
}

export default function Categories(props: Props) {

  const classes = useStyles()

  const { selected, all: categories } = props.categoriesState

  const handleCategoryValueChange = (event: ChangeEvent<HTMLInputElement>) => {
    selected !== undefined && props.categoryActions.updateCategory({ index: selected, value: (event.target as HTMLInputElement).value })
  };

  return (
    <Card className={classes.root}>
      <Card className={classes.tabCard}>
        <CategoryAdd categoryActions={props.categoryActions} />
        <CategoryTabs {...props} />
      </Card>

      <TextField
        className={classes.textArea}
        multiline
        value={selected !== undefined ? categories[selected].value : ''}
        onChange={handleCategoryValueChange}
        disabled={selected === undefined}
        rows={29}
        color="secondary"
        variant="filled"
      />
    </Card>
  )
}
