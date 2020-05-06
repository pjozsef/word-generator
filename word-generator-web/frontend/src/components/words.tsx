import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core';
import Card from '@material-ui/core/Card';
import { WordsState } from '../redux/slices/words-slice';

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        root: {
            flexFlow: 'column nowrap',
            overflow: 'scroll',
            wordWrap: 'break-word',
            flexGrow: 0,
            flexBasis: '30vw',
            flexShrink: 0,
            textAlign: 'center',
            background: theme.palette.primary.light,
        },
        history: {
            color: theme.palette.primary.contrastText
        }
    })
)
type Props = {
    words: WordsState
}

export default function Words(props: Props) {
    const { words } = props
    const classes = useStyles(props)

    return (
        words.current.length>0 ? (
            <Card className={classes.root}>
                <Card >
                    <div >
                        {words.current.map((word) => (<p>{word}</p>))}
                    </div>
                </Card>
                <div className={classes.history}>
                    {words.history.map((word) => (<p>{word}</p>))}
                </div>
            </Card>) :
            <Card className={classes.root} style={{ visibility: 'hidden' }}></Card>
    )
}
