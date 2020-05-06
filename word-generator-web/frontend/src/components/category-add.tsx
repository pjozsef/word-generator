import { Button, Dialog, DialogTitle, DialogContent, TextField, DialogActions, createStyles, makeStyles, Theme } from "@material-ui/core";
import React from "react";
import { InlineIcon } from "@iconify/react";
import plusOutlined from '@iconify/icons-ant-design/plus-outlined'
import { CategoryActions } from "../redux/slices/categories-slice";
import onEnter from "../util/on-enter";

const useStyles = makeStyles((theme: Theme)=>
createStyles({
    darkText: {
        color: theme.palette.primary.dark
      },
      dialogActions: {
        display: 'flex',
        justifyContent: 'space-around'
    },
}))

type Props = {
    categoryActions: CategoryActions
}

export default function CategoryAdd(props: Props) {
    const { categoryActions: { addCategory } } = props
    const [open, setOpen] = React.useState(false);
    const [formCategoryName, setFormCategoryName] = React.useState('');
    const classes = useStyles()

    const handleFormCategoryNameChange = (event: React.ChangeEvent<HTMLInputElement>) => setFormCategoryName(event.target.value)

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleAdd = () => {
        handleClose();
        addCategory(formCategoryName)
        setFormCategoryName('')
    };

    return (
        <React.Fragment>
            <Button
                variant="contained"
                color="secondary"
                onClick={handleClickOpen}
            >
                <InlineIcon icon={plusOutlined} height="38" />
            </Button>

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
        </React.Fragment>)
}