import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserExtra from './user-extra';
import UserExtraDetail from './user-extra-detail';
import UserExtraUpdate from './user-extra-update';
import UserExtraDeleteDialog from './user-extra-delete-dialog';

const UserExtraRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserExtra />} />
    <Route path="new" element={<UserExtraUpdate />} />
    <Route path=":id">
      <Route index element={<UserExtraDetail />} />
      <Route path="edit" element={<UserExtraUpdate />} />
      <Route path="delete" element={<UserExtraDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserExtraRoutes;
