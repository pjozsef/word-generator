import React from 'react';
import { makeStyles, createStyles, useTheme, Theme } from '@material-ui/core/styles';

type Props = {
    href: string,
    children?: React.ReactNode,
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        root: {
            textDecoration: 'none',
            color: theme.palette.primary.contrastText,
            '&:hover': {
                color: theme.palette.secondary.light,
                textDecoration: 'underline'
            },
            '&:active': {
                color: theme.palette.secondary.dark,
                textDecoration: 'underline'
            }
        }
    })
)

export default function ThemedLink(props: Props) {
    const { href, children } = props

    const classes = useStyles(props)

    return (
        <a href={href} className={classes.root}>
            {children}
        </a>
    )
}