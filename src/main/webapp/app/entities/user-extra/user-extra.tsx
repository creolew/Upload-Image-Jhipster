import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUserExtra } from 'app/shared/model/user-extra.model';
import { getEntities } from './user-extra.reducer';

export const UserExtra = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const userExtraList = useAppSelector(state => state.userExtra.entities);
  const loading = useAppSelector(state => state.userExtra.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="user-extra-heading" data-cy="UserExtraHeading">
        <Translate contentKey="uploadImageApp.userExtra.home.title">User Extras</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="uploadImageApp.userExtra.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/user-extra/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="uploadImageApp.userExtra.home.createLabel">Create new User Extra</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {userExtraList && userExtraList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="uploadImageApp.userExtra.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="uploadImageApp.userExtra.frontImage">Front Image</Translate>
                </th>
                <th>
                  <Translate contentKey="uploadImageApp.userExtra.backImage">Back Image</Translate>
                </th>
                <th>
                  <Translate contentKey="uploadImageApp.userExtra.user">User</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {userExtraList.map((userExtra, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/user-extra/${userExtra.id}`} color="link" size="sm">
                      {userExtra.id}
                    </Button>
                  </td>
                  <td>{userExtra.frontImage}</td>
                  <td>{userExtra.backImage}</td>
                  <td>{userExtra.user ? userExtra.user.id : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/user-extra/${userExtra.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/user-extra/${userExtra.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/user-extra/${userExtra.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="uploadImageApp.userExtra.home.notFound">No User Extras found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default UserExtra;
