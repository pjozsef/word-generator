import React from 'react';
import { makeStyles, createStyles } from '@material-ui/core/styles';

type Props = {
    rotation?: number,
    cyclic?: boolean,
    duration?: string,
    timing?: 'linear' | 'ease' | 'ease-in' | 'ease-out' | 'ease-in-out'
    children: React.ReactNode,
}

type FixRotateProps = {
    rotation: number,
    children: React.ReactNode
}

type CyclicRotateProps = {
    duration?: string,
    timing?: 'linear' | 'ease' | 'ease-in' | 'ease-out' | 'ease-in-out'
    children: React.ReactNode
}

const fixUseStyles = makeStyles(() => {
    return createStyles({
        root: {
            display: 'inline-block',
            transform: (props: FixRotateProps) => 'rotate('+props.rotation+'deg)'
        }
    })
})

const cyclicUseStyles = makeStyles(() => {
    return createStyles({
        '@keyframes spin': {
            from: { transform: 'rotate(0deg)' },
            to: { transform: 'rotate(360deg)' }
        },
        root: {
            display: 'inline-block',
            animationName: '$spin',
            animationDuration: (props: CyclicRotateProps) => props.duration || '1s',
            animationIterationCount: 'infinite',
            animationTimingFunction: (props: CyclicRotateProps) => props.timing ||'ease'
        }
    })
})

function FixRotate(props: FixRotateProps) {
    const classes = fixUseStyles(props)
    return <div className={classes.root}>{props.children}</div>
}

function CyclicRotate(props: CyclicRotateProps) {
    const classes = cyclicUseStyles(props)
    return <span className={classes.root}>{props.children}</span>
}

export default function Rotate(props: Props) {
    const { children, cyclic, rotation, duration, timing } = props

    if (rotation) {
        return <FixRotate rotation={rotation}>{children}</FixRotate>
    }
    if (cyclic) {
        return <CyclicRotate duration={duration} timing={timing}>{children}</CyclicRotate>
    }
    return <div>{children}</div>
}