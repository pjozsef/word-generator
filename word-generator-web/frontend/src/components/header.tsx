import React from 'react';
import './header.css'
import TextField from '@material-ui/core/TextField';
import { Button } from '@material-ui/core';

type Props = {
    command: string,
    onType: (event: React.ChangeEvent<HTMLInputElement>) => void,
    generateWord: () => void
}

export default class Header extends React.PureComponent<Props> {

    handleEnter = (event: React.KeyboardEvent) => {
        const {generateWord} = this.props
        
        if(event.nativeEvent.keyCode === 13){
            generateWord()
        }
    }

    render() {
        const { command, onType, generateWord } = this.props
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
                        onKeyPress={this.handleEnter}
                        variant="outlined" />
                    <Button
                        className="generate"
                        variant="contained"
                        color="secondary"
                        onClick={generateWord}>
                        Generate
                        </Button>
                </div>
            </div>
        )
    }
}
