import React from 'react';
import TextField from '@material-ui/core/TextField';
import { Button, makeStyles, Theme, createStyles } from '@material-ui/core';
import Card from '@material-ui/core/Card';
import { RequestStatus } from '../redux/slices/requests-slice';
import { InlineIcon } from '@iconify/react';
import Rotate from './rotate';
import hourglassOutlined from '@iconify/icons-ant-design/hourglass-outlined';

type Props = {
    command: string,
    onType: (event: React.ChangeEvent<HTMLInputElement>) => void,
    generateWord: () => void,
    isFetching: RequestStatus
}

const renderButton = (props: Props, classes: Record<"spinner" | "generateButton", string>) => {
    const { isFetching, generateWord } = props
    const { generateButton, spinner } = classes

    return isFetching === "OFF" ?
        <Button
            className={generateButton}
            variant="contained"
            color="secondary"
            onClick={generateWord}>
            Generate</Button> :
        <div className={generateButton}>
            <Rotate cyclic><InlineIcon className={spinner} icon={hourglassOutlined} height="38" /></Rotate>
        </div>
}

const useStyle = makeStyles((theme: Theme) =>
    createStyles({
        root: {
            background: theme.palette.primary.main,
            display: 'flex',
            flexFlow: 'column nowrap',
            justifyContent: 'space-around',
            alignItems: 'stretch'
        },
        h1: {
            margin: theme.spacing(3, 4, 1)
        },
        inputDiv: {
            display: 'flex',
            margin: theme.spacing(3, 0, 4),
            '& > *': {
                margin: theme.spacing(0, 1)
            }
        },
        leftGap: {
            flexGrow: 3
        },
        expessionInput: {
            flexGrow: 6
        },
        generateButtonContainer: {
            display: 'flex',
            flexGrow: 2,
            flexBasis: 2,
        },
        generateButton: {
            margin: 'auto'
        },
        spinner: {
            color: theme.palette.secondary.main,
            filter: 'drop-shadow( 0px 0px 2px rgba(0, 0, 0, .7))'
        }
    })
)

export default function Header(props: Props) {
    const { command, onType } = props

    const handleEnter = (event: React.KeyboardEvent) => {
        const { generateWord } = props

        if (event.nativeEvent.keyCode === 13) {
            generateWord()
        }
    }

    const classes = useStyle(props)

    return (
        <Card className={classes.root}>
            <h1 className={classes.h1}>Word generator</h1>
            <div className={classes.inputDiv}>
                <div className={classes.leftGap} />
                <TextField
                    className={classes.expessionInput}
                    label="Expression"
                    color="secondary"
                    size="small"
                    value={command}
                    onChange={onType}
                    onKeyPress={handleEnter}
                    variant="outlined" />

                <div className={classes.generateButtonContainer}>
                    {renderButton(props, classes)}
                </div>
            </div>
        </Card>
    )
}
