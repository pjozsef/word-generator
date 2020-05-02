import React from 'react';
import { render } from '@testing-library/react';
import {App} from './app';

test('renders learn react link', () => {
  const { getByText } = render(<App />);
  const h1 = getByText(/Word generator/i);
  expect(h1).toBeInTheDocument();
});
