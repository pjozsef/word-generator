import React from 'react';
import { makeStyles, Theme, createStyles, useTheme, Tabs } from '@material-ui/core';
import Card from '@material-ui/core/Card';
import Tab from '@material-ui/core/Tab';

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        root: {
            display: 'flex',
            flexGrow: 1,
            flexShrink: 0,
          },
          tabCard: {
            display: 'flex',
            width: '25%',
            overflow: 'hidden'
          },
          tabs: {
            width: '100%',
            borderRight: `1px solid ${theme.palette.divider}`,
          },
    })
)
type Props = {

}

export default function Categories(props: Props) {
    const classes = useStyles()
    const theme = useTheme()
    return (
        <Card className={classes.root}>
        <Card className={classes.tabCard}>
          <Tabs
            orientation="vertical"
            variant="scrollable"
            value={0}
            onChange={() => {}}
            aria-label="Vertical tabs example"
            className={classes.tabs}
          >
            <Tab label="Item1" />
            <Tab label="Item 2" />
            <Tab label="Item 2" />
            <Tab label="Item 2" />
            <Tab label="Item 4" />
            <Tab label="Item 5" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 6" />
            <Tab label="Item 2" />
            <Tab label="Item 3" />
            <Tab label="Item 4" />
            <Tab label="Item 5" />
            <Tab label="Item 6" />
          </Tabs>
        </Card>

      </Card>
    )
}
