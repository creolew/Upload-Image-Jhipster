import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-extra.reducer';

export const UserExtraDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userExtraEntity = useAppSelector(state => state.userExtra.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userExtraDetailsHeading">
          <Translate contentKey="uploadImageApp.userExtra.detail.title">UserExtra</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userExtraEntity.id}</dd>
          <dt>
            <span id="frontImage">
              <Translate contentKey="uploadImageApp.userExtra.frontImage">Front Image</Translate>
            </span>
          </dt>
          <dd>{userExtraEntity.frontImage}</dd>
          <dt>
            <span id="backImage">
              <Translate contentKey="uploadImageApp.userExtra.backImage">Back Image</Translate>
            </span>
          </dt>
          <dd>{userExtraEntity.backImage}</dd>
          <dt>
            <Translate contentKey="uploadImageApp.userExtra.user">User</Translate>
          </dt>
          <dd>{userExtraEntity.user ? userExtraEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-extra" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-extra/${userExtraEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserExtraDetail;
