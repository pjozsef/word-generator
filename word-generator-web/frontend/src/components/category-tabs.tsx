import React from "react";
import { makeStyles, Theme, createStyles, Tabs, Button, TextField, Dialog, DialogTitle, DialogContent, DialogActions, IconButton } from '@material-ui/core';
import Tab from '@material-ui/core/Tab';
import { Icon } from '@iconify/react';
import editOutlined from '@iconify/icons-ant-design/edit-outlined';
import { CategoryActions, CategoriesState } from "../redux/slices/categories-slice";
import onEnter from "../util/on-enter";

type Props = {
    categoriesState: CategoriesState
    categoryActions: CategoryActions
}

function a11yProps(index: any) {
    return {
        id: `scrollable-auto-tab-${index}`,
        'aria-controls': `scrollable-auto-tabpanel-${index}`,
    };
}

type EditState = {
    open: boolean,
    index: number,
    editName: string
}

const createTabLabel = (categoryName: string, index: number, setEditOpen: (state: EditState) => void, classes: Record<string, string>) => {
    const editIcon = (<IconButton size="small" color="primary" component="span" onClick={() => setEditOpen({ open: true, index, editName: categoryName })}>
        <Icon icon={editOutlined} />
    </IconButton>)
    return <div className={classes.tabContent}>
        <span className={classes.sneakyTabIcon}>{editIcon}</span>
        <span className={classes.tabText}>{categoryName}</span>
    </div>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        tabs: {
            width: '100%',
            borderRight: `1px solid ${theme.palette.divider}`,
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
        tabText: {
            margin: 'auto',
            textOverflow: 'ellipsis',
            overflow: 'hidden'
        },
        darkText: {
            color: theme.palette.primary.dark
        },
        dialogActions: {
            display: 'flex',
            justifyContent: 'space-around'
        },
        indicator: {
            minWidth: 4,
        }
    }))

export default function CategoryTabs(props: Props) {
    const { categoriesState, categoryActions } = props
    const classes = useStyles()
    const [editOpen, setEditOpen] = React.useState({ open: false, index: 0, editName: '' });
    const handleEditCategoryName = (event: React.ChangeEvent<HTMLInputElement>) => setEditOpen({ ...editOpen, editName: event.target.value })
    const editHandleClose = () => {
        setEditOpen({ open: false, index: 0, editName: '' })
    }

    const handleTabEvent = (event: React.ChangeEvent<{}>, value: any) => {
        const { categoryActions: {selectCategory} } = props
        selectCategory(value)
      }

    const editHandleRename = () => {
        const { index, editName: name } = editOpen
        const oldName = categoriesState.all[index].name
        categoryActions.renameCategory({ index, name, oldName })
        editHandleClose()
    }

    const editHandleDelete = () => {
        const { index } = editOpen
        categoryActions.deleteCategory(index)
        editHandleClose()
    }
    return <React.Fragment>
        <Tabs
            TabIndicatorProps={{ className: classes.indicator }}
            orientation="vertical"
            variant="scrollable"
            value={categoriesState.selected}
            onChange={handleTabEvent}
            aria-label="Category tabs"
            className={classes.tabs}
        >
            {categoriesState.all.map((category, index) => 
                <Tab label={createTabLabel(category.name, index, setEditOpen, classes)} {...a11yProps({ index })} />
            )}
        </Tabs>

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
}