import React from 'react';
import './header.css'
import TextField from '@material-ui/core/TextField';
import { Button, makeStyles, Theme, createStyles } from '@material-ui/core';
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

const renderButton = (props: Props, spinnerClass: string) => {
    const { isFetching, generateWord } = props
    return isFetching === "OFF" ?
        <Button
            variant="contained"
            color="secondary"
            onClick={generateWord}>
            Generate</Button> :
        <Rotate cyclic><InlineIcon className={spinnerClass} icon={hourglassOutlined} height="40" /></Rotate>
}

const useStyle = makeStyles((theme: Theme) =>
    createStyles({
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
        <div id="header">
            <h1>Word generator</h1>
            <div className="commandDiv">
                <TextField
                    className="commandInput"
                    label="Expression"
                    color="secondary"
                    size="small"
                    value={command}
                    onChange={onType}
                    onKeyPress={handleEnter}
                    variant="outlined" />

                <div
                    className="generate">
                    {renderButton(props, classes.spinner)}
                </div>
            </div>
        </div>
    )
}
