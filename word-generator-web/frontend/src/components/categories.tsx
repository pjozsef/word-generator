import React, { ChangeEvent } from 'react';
import { makeStyles, Theme, createStyles, Tabs, Button, TextField, Dialog, DialogTitle, DialogContent, useTheme, DialogActions, IconButton } from '@material-ui/core';
import Card from '@material-ui/core/Card';
import Tab from '@material-ui/core/Tab';
import { CategoryActions, CategoriesState } from '../redux/slices/categories-slice';
import { InlineIcon, Icon } from '@iconify/react';
import plusOutlined from '@iconify/icons-ant-design/plus-outlined';
import editOutlined from '@iconify/icons-ant-design/edit-outlined';


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
    tabs: {
      width: '100%',
      borderRight: `1px solid ${theme.palette.divider}`,
    },
    textArea: {
      width: '100%',
      flexGrow: 1,
      margin: theme.spacing(2),

      selfAlign: 'center'
    },
    darkText: {
      color: theme.palette.primary.dark
    },
    dialogActions: {
      display: 'flex',
      justifyContent: 'space-around'
    },
    tabContent: {
      display: 'flex',
      alignItems: 'center',
      width: '100%',
      '&:hover > *': {
        opacity: '1'
      }
    },
    sneakyTabIcon: {
      opacity: '0',
      transition: 'all 300ms'
    },
    indicator: {
      minWidth: 4,
    },
    tabText: {
      margin: 'auto',
      textOverflow: 'ellipsis',
      overflow: 'hidden'
    }
  })
)
type Props = {
  categoryActions: CategoryActions,
  categoriesState: CategoriesState
}

function a11yProps(index: any) {
  return {
    id: `scrollable-auto-tab-${index}`,
    'aria-controls': `scrollable-auto-tabpanel-${index}`,
  };
}

export default function Categories(props: Props) {

  const classes = useStyles()
  const theme = useTheme()

  const handleKeys = (event: ChangeEvent<HTMLInputElement>) => {
    const { updateCategory } = props.categoryActions

    const payload = {
      index: 0,
      value: (event.target as HTMLInputElement).value
    }
    updateCategory(payload)
  }

  const handleTabEvent = (event: React.ChangeEvent<{}>, value: any) => {
    const { selectCategory } = props.categoryActions
    selectCategory(value)
  }

  const { selected, all: categories } = props.categoriesState

  const [open, setOpen] = React.useState(false);
  const [editOpen, setEditOpen] = React.useState({ open: false, index: 0, editName: '' });
  const [formCategoryName, setFormCategoryName] = React.useState('');
  const handleFormCategoryNameChange = (event: React.ChangeEvent<HTMLInputElement>) => setFormCategoryName(event.target.value)
  const handleEditCategoryName = (event: React.ChangeEvent<HTMLInputElement>) => setEditOpen({ ...editOpen, editName: event.target.value })

  const onEnter = (action: () => void) => {
    return (event: React.KeyboardEvent) => {
      if (event.nativeEvent.keyCode === 13) {
        action()
      }
    }
  }

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleAdd = () => {
    handleClose();
    props.categoryActions.addCategory(formCategoryName)
    setFormCategoryName('')
  };

  const editHandleClose = () => {
    setEditOpen({ open: false, index: 0, editName: '' })
  }

  const editHandleRename = () => {
    const { index, editName: name } = editOpen
    props.categoryActions.renameCategory({ index, name })
    editHandleClose()
  }

  const editHandleDelete = () => {
    const { index } = editOpen
    props.categoryActions.deleteCategory(index)
    editHandleClose()
  }

  const handleCategoryValueChange = (event: ChangeEvent<HTMLInputElement>) => {
    console.log(selected !== undefined)
    selected !== undefined && props.categoryActions.updateCategory({ index: selected, value: (event.target as HTMLInputElement).value })
  };

  const createTabLabel = (categoryName: string, index: number) => {
    const editIcon = (<IconButton size="small" color="primary" component="span" onClick={() => setEditOpen({ open: true, index, editName: categoryName })}>
      <Icon icon={editOutlined} />
    </IconButton>)
    return <div className={classes.tabContent}>
      <span className={classes.sneakyTabIcon}>{editIcon}</span>
      <span className={classes.tabText}>{categoryName}</span>
    </div>
  }


  return (
    <React.Fragment>
      <Card className={classes.root}>
        <Card className={classes.tabCard}>
          <Button
            variant="contained"
            color="secondary"
            onClick={handleClickOpen}
          >
            <InlineIcon icon={plusOutlined} height="38" />
          </Button>
          <Tabs
            TabIndicatorProps={{ className: classes.indicator }}
            orientation="vertical"
            variant="scrollable"
            value={selected}
            onChange={handleTabEvent}
            aria-label="Vertical tabs example"
            className={classes.tabs}
          >

            {categories.map((category, index) => (
              <Tab label={createTabLabel(category.name, index)} {...a11yProps({ index })} />
            ))}
          </Tabs>
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

      <Dialog open={open} onClose={handleClose}>
        <DialogTitle className={classes.darkText}>Add new category</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Category name"
            type="text"
            value={formCategoryName}
            onChange={handleFormCategoryNameChange}
            onKeyPress={onEnter(handleAdd)}
            fullWidth
            InputProps={{
              className: classes.darkText
            }}
          />
        </DialogContent>
        <DialogActions className={classes.dialogActions}>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleAdd} variant="contained" color="secondary">
            Add
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={editOpen.open} onClose={editHandleClose}>
        <DialogTitle className={classes.darkText}>Edit category</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Category name"
            type="text"
            value={editOpen.editName}
            onChange={handleEditCategoryName}
            onKeyPress={onEnter(editHandleRename)}
            fullWidth
            InputProps={{
              className: classes.darkText
            }}
          />
        </DialogContent>
        <DialogActions className={classes.dialogActions}>
          <Button onClick={editHandleDelete} variant="contained" color="primary">
            Delete
          </Button>
          <Button onClick={editHandleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={editHandleRename} variant="contained" color="secondary">
            Rename
          </Button>
        </DialogActions>
      </Dialog>
    </React.Fragment>
  )
}
